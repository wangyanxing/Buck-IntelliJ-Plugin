package com.intellij.plugin.buck.actions;

import com.intellij.codeInsight.lookup.LookupManager;
import com.intellij.featureStatistics.FeatureUsageTracker;
import com.intellij.icons.AllIcons;
import com.intellij.ide.DataManager;
import com.intellij.ide.ui.laf.darcula.ui.DarculaTextBorder;
import com.intellij.ide.ui.laf.darcula.ui.DarculaTextFieldUI;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.actionSystem.ex.AnActionListener;
import com.intellij.openapi.actionSystem.impl.ActionToolbarImpl;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actions.TextComponentEditorAction;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.util.ProgressIndicatorBase;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.ComponentPopupBuilder;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.util.ActionCallback;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.plugin.buck.actions.renderer.BuckTargetPsiRenderer;
import com.intellij.plugin.buck.build.BuckBuildTarget;
import com.intellij.plugin.buck.build.BuckBuildTargetAliasParser;
import com.intellij.plugin.buck.config.BuckSettingsProvider;
import com.intellij.plugin.buck.ui.BuckToolWindowFactory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.codeStyle.MinusculeMatcher;
import com.intellij.psi.codeStyle.NameUtil;
import com.intellij.ui.*;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.ui.border.CustomLineBorder;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.panels.NonOpaquePanel;
import com.intellij.ui.popup.AbstractPopup;
import com.intellij.ui.popup.PopupPositionManager;
import com.intellij.util.Alarm;
import com.intellij.util.ReflectionUtil;
import com.intellij.util.text.Matcher;
import com.intellij.util.ui.EmptyIcon;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.StatusText;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChooseTargetAction extends DumbAwareAction implements DataProvider {

  public static final String SE_HISTORY_KEY = "BuckTargetHistoryKey";

  public static final int SEARCH_FIELD_COLUMNS = 25;
  private static final int POPUP_MAX_WIDTH = 600;
  private static final int MAX_RECENT_TARGETS = 10;

  private static AtomicBoolean showAll = new AtomicBoolean(false);

  private Project myProject;
  private MySearchTextField myPopupField;
  private HistoryItem myHistoryItem;
  private int myHistoryIndex = 0;
  private JBList myList;
  private boolean mySkipFocusGain = false;
  private Editor myEditor;
  private JBPopup myPopup;
  private CalcThread myCalcThread;
  private MyListRenderer myRenderer;
  private volatile JBPopup myBalloon;
  private Alarm myAlarm =
      new Alarm(Alarm.ThreadToUse.SWING_THREAD, ApplicationManager.getApplication());
  private volatile ActionCallback myCurrentWorker = ActionCallback.DONE;
  private int myPopupActualWidth;
  private Map<String, String> myConfigurables = new HashMap<String, String>();
  private Component myFocusOwner;
  private Component myContextComponent;
  private AnActionEvent myActionEvent;

  public ChooseTargetAction() {
    super("Choose target", "Choose build target", AllIcons.Actions.Preview);
  }

  public ChooseTargetAction init(Project project) {
    myProject = project;

    updateComponents();
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        onFocusLost();
      }
    });
    return this;
  }

  private static Font getTitleFont() {
    return UIUtil.getLabelFont().deriveFont(UIUtil.getFontSize(UIUtil.FontSize.SMALL));
  }

  @Override
  public void actionPerformed(AnActionEvent e) {
    String path = e.getProject().getBasePath();
    BuckBuildTargetAliasParser.parseAlias(path);

    if (myBalloon != null && myBalloon.isVisible()) {
      showAll.set(!showAll.get());
      rebuildList(myPopupField.getText());
      return;
    }
    myCurrentWorker = ActionCallback.DONE;
    if (e != null) {
      myEditor = e.getData(CommonDataKeys.EDITOR);
    }
    if (e == null && myFocusOwner != null) {
      e = new AnActionEvent(null, DataManager.getInstance().getDataContext(myFocusOwner),
          ActionPlaces.UNKNOWN, getTemplatePresentation(), ActionManager.getInstance(), 0);
    }
    if (e == null) return;
    final Project project = e.getProject();
    if (project == null) return;

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        LookupManager.getInstance(project).hideActiveLookup();
      }
    });

    updateComponents();
    myContextComponent = PlatformDataKeys.CONTEXT_COMPONENT.getData(e.getDataContext());
    Window wnd = myContextComponent != null ? SwingUtilities.windowForComponent(myContextComponent)
        : KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusedWindow();
    if (wnd == null && myContextComponent instanceof Window) {
      wnd = (Window) myContextComponent;
    }
    if (wnd == null || wnd.getParent() != null) return;
    myActionEvent = e;
    if (myPopupField != null) {
      Disposer.dispose(myPopupField);
    }
    myPopupField = new MySearchTextField();
    myPopupField.getTextEditor().addKeyListener(new KeyAdapter() {
      @Override
      public void keyTyped(KeyEvent e) {
        myHistoryIndex = 0;
        myHistoryItem = null;
      }

      @Override
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
          myList.repaint();
        }
      }

      @Override
      public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
          myList.repaint();
        }
      }
    });

    initSearchField(myPopupField);
    myPopupField.setOpaque(false);
    final JTextField editor = myPopupField.getTextEditor();
    editor.setColumns(SEARCH_FIELD_COLUMNS);
    final JPanel panel = new JPanel(new BorderLayout()) {
      @Override
      protected void paintComponent(Graphics g) {
        final Gradient gradient = getGradientColors();
        ((Graphics2D) g).setPaint(new GradientPaint(0, 0, gradient.getStartColor(),
            0, getHeight(), gradient.getEndColor()));
        g.fillRect(0, 0, getWidth(), getHeight());
      }
    };

    final JLabel title = new JLabel("Choose Buck Target:");
    final JPanel topPanel = new NonOpaquePanel(new BorderLayout());
    title.setForeground(new JBColor(Gray._240, Gray._200));
    if (SystemInfo.isMac) {
      title.setFont(title.getFont().deriveFont(Font.BOLD, title.getFont().getSize() - 1f));
    } else {
      title.setFont(title.getFont().deriveFont(Font.BOLD));
    }
    topPanel.add(title, BorderLayout.WEST);
    final JPanel controls = new JPanel(new BorderLayout());
    controls.setOpaque(false);

    topPanel.add(controls, BorderLayout.EAST);
    panel.add(myPopupField, BorderLayout.CENTER);
    panel.add(topPanel, BorderLayout.NORTH);
    panel.setBorder(IdeBorderFactory.createEmptyBorder(3, 5, 4, 5));
    DataManager.registerDataProvider(panel, this);
    final ComponentPopupBuilder builder = JBPopupFactory.getInstance()
        .createComponentPopupBuilder(panel, editor);
    myBalloon = builder
        .setCancelOnClickOutside(true)
        .setModalContext(false)
        .setRequestFocus(true)
        .setCancelCallback(new Computable<Boolean>() {
          @Override
          public Boolean compute() {
            return !mySkipFocusGain;
          }
        })
        .createPopup();
    myBalloon.getContent().setBorder(JBUI.Borders.empty());
    final Window window = WindowManager.getInstance().suggestParentWindow(project);

    project.getMessageBus().connect(myBalloon).subscribe(DumbService.DUMB_MODE,
        new DumbService.DumbModeListener() {
          @Override
          public void enteredDumbMode() {
          }

          @Override
          public void exitDumbMode() {
            rebuildList(myPopupField.getText());
          }
        });

    Component parent = UIUtil.findUltimateParent(window);
    registerDataProvider(panel);
    final RelativePoint showPoint;
    if (parent != null) {
      showPoint = new RelativePoint(
          parent,
          new Point((parent.getSize().width - panel.getPreferredSize().width) / 2,
              (parent.getSize().height - panel.getPreferredSize().height) / 2));
    } else {
      showPoint = JBPopupFactory.getInstance().guessBestPopupLocation(e.getDataContext());
    }

    myList.setFont(UIUtil.getListFont());
    myBalloon.show(showPoint);
    initSearchActions(myBalloon, myPopupField);
    IdeFocusManager focusManager = IdeFocusManager.getInstance(project);
    focusManager.requestFocus(editor, true);
    FeatureUsageTracker.getInstance().triggerFeatureUsed(IdeActions.ACTION_SEARCH_EVERYWHERE);
  }

  private void registerDataProvider(JPanel panel) {
    DataManager.registerDataProvider(panel, new DataProvider() {
      @Nullable
      @Override
      public Object getData(@NonNls String dataId) {
        final Object value = myList.getSelectedValue();
        if (CommonDataKeys.PSI_ELEMENT.is(dataId) && value instanceof PsiElement) {
          return value;
        } else if (CommonDataKeys.VIRTUAL_FILE.is(dataId) && value instanceof VirtualFile) {
          return value;
        }
        return null;
      }
    });
  }

  private void updatePopupBounds() {
    if (myPopup == null || !myPopup.isVisible()) {
      return;
    }
    final Container parent = getField().getParent();
    final Dimension size = myList.getParent().getParent().getPreferredSize();
    size.width = myPopupActualWidth - 2;
    if (size.width + 2 < parent.getWidth()) {
      size.width = parent.getWidth();
    }
    if (myList.getItemsCount() == 0) {
      size.height = JBUI.scale(30);
    }
    Dimension sz = new Dimension(size.width, myList.getPreferredSize().height);
    if (!SystemInfo.isMac) {
      if ((sz.width > POPUP_MAX_WIDTH || sz.height > POPUP_MAX_WIDTH)) {
        final JBScrollPane pane = new JBScrollPane();
        final int extraWidth = pane.getVerticalScrollBar().getWidth() + 1;
        final int extraHeight = pane.getHorizontalScrollBar().getHeight() + 1;
        sz = new Dimension(
            Math.min(POPUP_MAX_WIDTH, Math.max(getField().getWidth(), sz.width + extraWidth)),
            Math.min(POPUP_MAX_WIDTH, sz.height + extraHeight));
        sz.width += 20;
        sz.height += 2;
      } else {
        sz.width += 2;
        sz.height += 2;
      }
    }
    sz.width = Math.max(sz.width, myPopup.getSize().width);
    myPopup.setSize(sz);
    if (myActionEvent != null && myActionEvent.getInputEvent() == null) {
      final Point p = parent.getLocationOnScreen();
      p.y += parent.getHeight();
      if (parent.getWidth() < sz.width) {
        p.x -= sz.width - parent.getWidth();
      }
      myPopup.setLocation(p);
    } else {
      try {
        adjustPopup();
      } catch (Exception ignore) {
      }
    }
  }

  private void adjustPopup() {
    final Dimension d = PopupPositionManager.PositionAdjuster.getPopupSize(myPopup);
    final JComponent myRelativeTo = myBalloon.getContent();
    Point myRelativeOnScreen = myRelativeTo.getLocationOnScreen();
    Rectangle screen = ScreenUtil.getScreenRectangle(myRelativeOnScreen);
    Rectangle popupRect = null;
    Rectangle r = new Rectangle(
        myRelativeOnScreen.x,
        myRelativeOnScreen.y + myRelativeTo.getHeight(),
        d.width,
        d.height);

    if (screen.contains(r)) {
      popupRect = r;
    }

    if (popupRect != null) {
      Point location = new Point(r.x, r.y);
      if (!location.equals(myPopup.getLocationOnScreen())) {
        myPopup.setLocation(location);
      }
    } else {
      if (r.y + d.height > screen.y + screen.height) {
        r.height = screen.y + screen.height - r.y - 2;
      }
      if (r.width > screen.width) {
        r.width = screen.width - 50;
      }
      if (r.x + r.width > screen.x + screen.width) {
        r.x = screen.x + screen.width - r.width - 2;
      }

      myPopup.setSize(r.getSize());
      myPopup.setLocation(r.getLocation());
    }
  }

  protected void resetFields() {
    if (myBalloon != null) {
      myBalloon.cancel();
      myBalloon = null;
    }
    myCurrentWorker.doWhenProcessed(new Runnable() {
      @Override
      public void run() {
        final Object lock = myCalcThread;
        if (lock != null) {
          synchronized (lock) {
            myConfigurables.clear();
            myContextComponent = null;
            myFocusOwner = null;
            myRenderer.myProject = null;
            myPopup = null;
            myHistoryIndex = 0;
            myPopupActualWidth = 0;
            myCurrentWorker = ActionCallback.DONE;
            showAll.set(false);
            myCalcThread = null;
            myEditor = null;
          }
        }
      }
    });
    mySkipFocusGain = false;
  }

  private void initSearchActions(JBPopup balloon, MySearchTextField searchTextField) {
    final JTextField editor = searchTextField.getTextEditor();
    new DumbAwareAction() {
      @Override
      public void actionPerformed(AnActionEvent e) {
        jumpNextGroup(true);
      }
    }.registerCustomShortcutSet(CustomShortcutSet.fromString("TAB"), editor, balloon);
    new DumbAwareAction() {
      @Override
      public void actionPerformed(AnActionEvent e) {
        jumpNextGroup(false);
      }
    }.registerCustomShortcutSet(CustomShortcutSet.fromString("shift TAB"), editor, balloon);
    new DumbAwareAction() {
      @Override
      public void actionPerformed(AnActionEvent e) {
        if (myBalloon != null && myBalloon.isVisible()) {
          myBalloon.cancel();
        }
        if (myPopup != null && myPopup.isVisible()) {
          myPopup.cancel();
        }
      }
    }.registerCustomShortcutSet(CustomShortcutSet.fromString("ESCAPE"), editor, balloon);
    new DumbAwareAction() {
      @Override
      public void actionPerformed(AnActionEvent e) {
        final int index = myList.getSelectedIndex();
        if (index != -1) {
          chooseTarget(index);
        }
      }
    }.registerCustomShortcutSet(
        CustomShortcutSet.fromString("ENTER", "shift ENTER"),
        editor,
        balloon);
    new DumbAwareAction() {
      @Override
      public void actionPerformed(AnActionEvent e) {
        final PropertiesComponent storage = PropertiesComponent.getInstance(e.getProject());
        final String[] values = storage.getValues(SE_HISTORY_KEY);
        if (values != null) {
          if (values.length > myHistoryIndex) {
            final List<String> data = StringUtil.split(values[myHistoryIndex], "\t");
            myHistoryItem = new HistoryItem(data.get(0), data.get(1), data.get(2));
            myHistoryIndex++;
            editor.setText(myHistoryItem.pattern);
            editor.setCaretPosition(myHistoryItem.pattern.length());
            editor.moveCaretPosition(0);
          }
        }
      }

      @Override
      public void update(AnActionEvent e) {
        e.getPresentation().setEnabled(editor.getCaretPosition() == 0);
      }
    }.registerCustomShortcutSet(CustomShortcutSet.fromString("LEFT"), editor, balloon);
  }

  private void jumpNextGroup(boolean forward) {
    final int index = myList.getSelectedIndex();
    final SearchListModel model = getModel();
    if (index >= 0) {
      final int newIndex = forward ? model.next(index) : model.prev(index);
      myList.setSelectedIndex(newIndex);
      int more = model.next(newIndex) - 1;
      if (more < newIndex) {
        more = myList.getItemsCount() - 1;
      }
      ListScrollingUtil.ensureIndexIsVisible(myList, more, forward ? 1 : -1);
      ListScrollingUtil.ensureIndexIsVisible(myList, newIndex, forward ? 1 : -1);
    }
  }

  private static Gradient getGradientColors() {
    return new Gradient(
        new JBColor(new Color(101, 147, 242), new Color(64, 80, 94)),
        new JBColor(new Color(46, 111, 205), new Color(53, 65, 87)));
  }

  private void initSearchField(final MySearchTextField search) {
    final JTextField editor = search.getTextEditor();
    editor.getDocument().addDocumentListener(new DocumentAdapter() {
      @Override
      protected void textChanged(DocumentEvent e) {
        final String pattern = editor.getText();
        if (editor.hasFocus()) {
          rebuildList(pattern);
        }
      }
    });
    editor.addFocusListener(new FocusAdapter() {
      @Override
      public void focusGained(FocusEvent e) {
        if (mySkipFocusGain) {
          mySkipFocusGain = false;
          return;
        }
        String text = "";
        if (myEditor != null) {
          text = myEditor.getSelectionModel().getSelectedText();
          text = text == null ? "" : text.trim();
        }
        search.setText(text);
        search.getTextEditor().setForeground(UIUtil.getLabelForeground());
        editor.setColumns(SEARCH_FIELD_COLUMNS);
        SwingUtilities.invokeLater(new Runnable() {
          @Override
          public void run() {
            final JComponent parent = (JComponent) editor.getParent();
            parent.revalidate();
            parent.repaint();
          }
        });
        rebuildList(text);
      }

      @Override
      public void focusLost(FocusEvent e) {
        if (myPopup instanceof AbstractPopup && myPopup.isVisible()
            && ((myList == e.getOppositeComponent()) ||
            ((AbstractPopup) myPopup).getPopupWindow() == e.getOppositeComponent())) {
          return;
        }
        onFocusLost();
      }
    });
  }

  private void rebuildList(final String pattern) {
    assert EventQueue.isDispatchThread() : "Must be EDT";
    if (myCalcThread != null && !myCurrentWorker.isProcessed()) {
      myCurrentWorker = myCalcThread.cancel();
    }
    if (myCalcThread != null && !myCalcThread.isCanceled()) {
      myCalcThread.cancel();
    }
    final Project project =
        CommonDataKeys.PROJECT.getData(
            DataManager.getInstance().getDataContext(getField().getTextEditor()));

    assert project != null;
    myRenderer.myProject = project;
    final Runnable run = new Runnable() {
      @Override
      public void run() {
        myCalcThread = new CalcThread(pattern, false);
        myPopupActualWidth = 0;
        myCurrentWorker = myCalcThread.start();
      }
    };
    if (myCurrentWorker.isDone()) {
      myCurrentWorker.doWhenDone(run);
    } else {
      myCurrentWorker.doWhenRejected(run);
    }
  }

  private void updateComponents() {
    myRenderer = new MyListRenderer();
    myList = new JBList() {
      int lastKnownHeight = JBUI.scale(30);

      @Override
      public Dimension getPreferredSize() {
        final Dimension size = super.getPreferredSize();
        if (size.height == -1) {
          size.height = lastKnownHeight;
        } else {
          lastKnownHeight = size.height;
        }
        return new Dimension(Math.max(myBalloon.getSize().width,
            Math.min(size.width - 2, POPUP_MAX_WIDTH)),
            myList.isEmpty() ? JBUI.scale(30) : size.height);
      }

      @Override
      public void clearSelection() {
        //avoid blinking
      }

      @Override
      public Object getSelectedValue() {
        try {
          return super.getSelectedValue();
        } catch (Exception e) {
          return null;
        }
      }
    };
    myList.setCellRenderer(myRenderer);
    myList.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        e.consume();
        final int i = myList.locationToIndex(e.getPoint());
        if (i != -1) {
          mySkipFocusGain = true;
          getField().requestFocus();
          SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
              myList.setSelectedIndex(i);
              chooseTarget(i);
            }
          });
        }
      }
    });
  }

  private void chooseTarget(final int index) {
    final Object value = myList.getSelectedValue();
    if (value instanceof BuckBuildTarget) {
      // Save history
      String alias = ((BuckBuildTarget) value).getAlias();

      if (BuckSettingsProvider.getInstance().getState().lastAlias != null) {
        BuckSettingsProvider.getInstance().getState().lastAlias.put(myProject.getBasePath(), alias);
      }

      BuckToolWindowFactory.updateBuckToolWindowTitle(myProject);
    }

    IdeFocusManager focusManager =
        IdeFocusManager.findInstanceByComponent(getField().getTextEditor());
    if (myPopup != null && myPopup.isVisible()) {
      myPopup.cancel();
    }
    focusManager.requestDefaultFocus(true);
  }

  private static JComponent createTitle(String titleText) {
    JLabel titleLabel = new JLabel(titleText);
    titleLabel.setFont(getTitleFont());
    titleLabel.setForeground(UIUtil.getLabelDisabledForeground());
    final Color bg = UIUtil.getListBackground();
    SeparatorComponent separatorComponent =
        new SeparatorComponent(
            titleLabel.getPreferredSize().height / 2,
            new JBColor(Gray._220, Gray._80),
            null);

    JPanel result = new JPanel(new BorderLayout(5, 10));
    result.add(titleLabel, BorderLayout.WEST);
    result.add(separatorComponent, BorderLayout.CENTER);
    result.setBackground(bg);

    return result;
  }

  private SearchTextField getField() {
    return myPopupField;
  }

  private SearchListModel getModel() {
    return (SearchListModel) myList.getModel();
  }

  private ActionCallback onFocusLost() {
    final ActionCallback result = new ActionCallback();
    UIUtil.invokeLaterIfNeeded(new Runnable() {
      @Override
      public void run() {
        try {
          if (myCalcThread != null) {
            myCalcThread.cancel();
          }
          myAlarm.cancelAllRequests();
          if (myBalloon != null &&
              !myBalloon.isDisposed() &&
              myPopup != null &&
              !myPopup.isDisposed()) {
            myBalloon.cancel();
            myPopup.cancel();
          }

          SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
              ActionToolbarImpl.updateAllToolbarsImmediately();
            }
          });
        } finally {
          result.setDone();
        }
      }
    });
    return result;
  }

  @Nullable
  @Override
  public Object getData(@NonNls String dataId) {
    return null;
  }

  private static class MySearchTextField extends SearchTextField
      implements DataProvider, Disposable {
    public MySearchTextField() {
      super(false);
      getTextEditor().setOpaque(false);
      getTextEditor().setUI((DarculaTextFieldUI) DarculaTextFieldUI.createUI(getTextEditor()));
      getTextEditor().setBorder(new DarculaTextBorder());

      getTextEditor().putClientProperty("JTextField.Search.noBorderRing", Boolean.TRUE);
      if (UIUtil.isUnderDarcula()) {
        getTextEditor().setBackground(Gray._45);
        getTextEditor().setForeground(Gray._240);
      }
    }

    @Override
    protected boolean isSearchControlUISupported() {
      return true;
    }

    @Override
    protected boolean hasIconsOutsideOfTextField() {
      return false;
    }

    @Override
    protected void showPopup() {
    }

    @Nullable
    @Override
    public Object getData(@NonNls String dataId) {
      if (PlatformDataKeys.PREDEFINED_TEXT.is(dataId)) {
        return getTextEditor().getText();
      }
      return null;
    }

    @Override
    public void dispose() {
    }
  }

  private static class HistoryItem {
    final String pattern, type, fqn;

    private HistoryItem(String pattern, String type, String fqn) {
      this.pattern = pattern;
      this.type = type;
      this.fqn = fqn;
    }

    public String toString() {
      return pattern + "\t" + type + "\t" + fqn;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      HistoryItem item = (HistoryItem) o;

      if (!pattern.equals(item.pattern)) return false;
      return true;
    }

    @Override
    public int hashCode() {
      return pattern.hashCode();
    }
  }

  static class TitleIndex {
    volatile int targets = -1;

    final String gotoTargetsTitle;

    TitleIndex() {
      gotoTargetsTitle = "Targets";
    }

    String getTitle(int index) {
      if (index == targets) return gotoTargetsTitle;
      return null;
    }
  }

  @SuppressWarnings("unchecked")
  private static class SearchListModel extends DefaultListModel {
    @SuppressWarnings("UseOfObsoleteCollectionType")
    Vector myDelegate;

    volatile TitleIndex titleIndex = new TitleIndex();

    private SearchListModel() {
      super();
      myDelegate = ReflectionUtil.getField(DefaultListModel.class, this, Vector.class, "delegate");
    }

    int next(int index) {
      int[] all = getAll();
      Arrays.sort(all);
      for (int next : all) {
        if (next > index) {
          return next;
        }
      }
      return 0;
    }

    int[] getAll() {
      return new int[]{
          titleIndex.targets,
      };
    }

    int prev(int index) {
      int[] all = getAll();
      Arrays.sort(all);
      for (int i = all.length - 1; i >= 0; i--) {
        if (all[i] != -1 && all[i] < index) {
          return all[i];
        }
      }
      return all[all.length - 1];
    }

    @Override
    public void addElement(Object obj) {
      myDelegate.add(obj);
    }

    public void update() {
      fireContentsChanged(this, 0, getSize() - 1);
    }
  }

  private class MyListRenderer extends ColoredListCellRenderer {
    ColoredListCellRenderer myLocation = new ColoredListCellRenderer() {
      @Override
      protected void customizeCellRenderer(JList list, Object value, int index, boolean selected, boolean hasFocus) {
        setPaintFocusBorder(false);
        append(myLocationString, SimpleTextAttributes.GRAYED_ATTRIBUTES);
        setIcon(myLocationIcon);
      }
    };
    BuckTargetPsiRenderer myTargetRenderer = new BuckTargetPsiRenderer();

    private String myLocationString;
    private Icon myLocationIcon;
    private Project myProject;
    private JPanel myMainPanel = new JPanel(new BorderLayout());
    private JLabel myTitle = new JLabel();

    @Override
    public void clear() {
      super.clear();
      myLocation.clear();
      myLocationString = null;
      myLocationIcon = null;
    }

    @Override
    public Component getListCellRendererComponent(
        JList list,
        Object value,
        int index,
        boolean isSelected,
        boolean cellHasFocus) {
      Component cmp;
      PsiElement file;
      myLocationString = null;
      String pattern = "*" + myPopupField.getText();
      Matcher matcher = NameUtil.buildMatcher(pattern, 0, true, true);

      VirtualFile buckFile = ((BuckBuildTarget) value).getVirtualFile();
      if (buckFile != null && myProject != null && ((buckFile.isDirectory() &&
          (file = PsiManager.getInstance(myProject).findDirectory(buckFile)) != null) ||
          (file = PsiManager.getInstance(myProject).findFile(buckFile)) != null)) {
        myTargetRenderer.setPatternMatcher(matcher);
        myTargetRenderer.setAlias(((BuckBuildTarget) value).getAlias());
        myTargetRenderer.setTarget(((BuckBuildTarget) value).getTarget());
        cmp = myTargetRenderer.getListCellRendererComponent(
            list, file, index, isSelected, cellHasFocus);
      } else {
        cmp = super.getListCellRendererComponent(list, value, index, isSelected, isSelected);
        final JPanel p = new JPanel(new BorderLayout());
        p.setBackground(UIUtil.getListBackground(isSelected));
        p.add(cmp, BorderLayout.CENTER);
        cmp = p;
      }

      Color bg = cmp.getBackground();
      if (bg == null) {
        cmp.setBackground(UIUtil.getListBackground(isSelected));
        bg = cmp.getBackground();
      }
      myMainPanel.setBorder(new CustomLineBorder(bg, 0, 0, 2, 0));
      String title = getModel().titleIndex.getTitle(index);
      myMainPanel.removeAll();
      if (title != null) {
        myTitle.setText(title);
        myMainPanel.add(createTitle(" " + title), BorderLayout.NORTH);
      }
      myMainPanel.add(cmp, BorderLayout.CENTER);
      final int width = myMainPanel.getPreferredSize().width;
      if (width > myPopupActualWidth) {
        myPopupActualWidth = width;
      }
      return myMainPanel;
    }

    @Override
    protected void customizeCellRenderer(
        JList list,
        Object value,
        int index,
        boolean selected,
        boolean hasFocus) {
    }

    public void recalculateWidth() {
      ListModel model = myList.getModel();
      myTitle.setIcon(EmptyIcon.ICON_16);
      myTitle.setFont(getTitleFont());

      int index = 0;
      while (index < model.getSize()) {
        String title = getModel().titleIndex.getTitle(index);
        if (title != null) {
          myTitle.setText(title);
        }
        index++;
      }

      myTitle.setForeground(Gray._122);
      myTitle.setAlignmentY(BOTTOM_ALIGNMENT);
    }
  }

  private class CalcThread implements Runnable {
    private final String pattern;
    private final ProgressIndicator myProgressIndicator = new ProgressIndicatorBase();
    private final ActionCallback myDone = new ActionCallback();
    private final SearchListModel myListModel;

    public CalcThread(String pattern, boolean reuseModel) {
      this.pattern = pattern;
      myListModel = reuseModel ? (SearchListModel) myList.getModel() : new SearchListModel();
    }

    @Override
    public void run() {
      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          try {
            check();
            myList.getEmptyText().setText("Searching...");

            SwingUtilities.invokeLater(new Runnable() {
              @Override
              public void run() {
                myList.getEmptyText().setText("Searching...");

                if (myList.getModel() instanceof SearchListModel) {
                  myAlarm.cancelAllRequests();
                  myAlarm.addRequest(new Runnable() {
                    @Override
                    public void run() {
                      if (!myDone.isRejected()) {
                        myList.setModel(myListModel);
                        updatePopup();
                      }
                    }
                  }, 50);
                } else {
                  myList.setModel(myListModel);
                }
              }
            });

            if (pattern.trim().length() == 0) {
              buildTargets("");
              return;
            }

            runReadAction(new Runnable() {
              public void run() {
                buildTargets(pattern);
              }
            }, true);
            check();
            updatePopup();
          } catch (ProcessCanceledException ignore) {
            myDone.setRejected();
          } catch (Exception e) {
            myDone.setRejected();
          } finally {
            if (!isCanceled()) {
              SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                  myList.getEmptyText().setText(StatusText.DEFAULT_EMPTY_TEXT);
                }
              });
              updatePopup();
            }
            if (!myDone.isProcessed()) {
              myDone.setDone();
            }
          }
        }
      });
    }

    private void runReadAction(Runnable action, boolean checkDumb) {
      if (!checkDumb || !DumbService.getInstance(myProject).isDumb()) {
        ApplicationManager.getApplication().runReadAction(action);
        updatePopup();
      }
    }

    private synchronized void buildTargets(String pattern) {
      final MinusculeMatcher matcher =
          new MinusculeMatcher("*" + pattern, NameUtil.MatchingCaseSensitivity.NONE);

      final ArrayList<BuckBuildTarget> targets =
          new ArrayList<BuckBuildTarget>();

      for (Map.Entry<String, String> entry : BuckBuildTargetAliasParser.sTargetAlias.entrySet()) {
        String alias = entry.getKey();
        String path = entry.getValue();
        if (StringUtil.isEmptyOrSpaces(pattern) || matcher.matches(alias)) {
          BuckBuildTarget target = new BuckBuildTarget(myProject, path, alias);
          if (target.getVirtualFile() != null) {
            targets.add(target);
          }
        }
        if (targets.size() > MAX_RECENT_TARGETS) {
          break;
        }
      }

      check();

      if (targets.size() > 0) {
        SwingUtilities.invokeLater(new Runnable() {
          @Override
          public void run() {
            if (isCanceled()) {
              return;
            }
            myListModel.titleIndex.targets = myListModel.size();
            for (Object target : targets) {
              myListModel.addElement(target);
            }
            updatePopup();
          }
        });
      }
    }

    protected void check() {
      myProgressIndicator.checkCanceled();
      if (myDone.isRejected()) {
        throw new ProcessCanceledException();
      }
      if (myBalloon == null || myBalloon.isDisposed()) {
        throw new ProcessCanceledException();
      }
    }

    @SuppressWarnings("SSBasedInspection")
    private void updatePopup() {
      check();
      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          myListModel.update();
          myList.revalidate();
          myList.repaint();

          myRenderer.recalculateWidth();
          if (myBalloon == null || myBalloon.isDisposed()) {
            return;
          }
          if (myPopup == null || !myPopup.isVisible()) {
            final ActionCallback callback =
                ListDelegationUtil.installKeyboardDelegation(getField().getTextEditor(), myList);
            JBScrollPane content = new JBScrollPane(myList) {
              {
                if (UIUtil.isUnderDarcula()) {
                  setBorder(null);
                }
              }

              @Override
              public Dimension getPreferredSize() {
                Dimension size = super.getPreferredSize();
                Dimension listSize = myList.getPreferredSize();
                if (size.height > listSize.height || myList.getModel().getSize() == 0) {
                  size.height = Math.max(JBUI.scale(30), listSize.height);
                }

                if (size.width < myBalloon.getSize().width) {
                  size.width = myBalloon.getSize().width;
                }
                return size;
              }
            };
            content.setMinimumSize(new Dimension(myBalloon.getSize().width, 30));
            final ComponentPopupBuilder builder = JBPopupFactory.getInstance()
                .createComponentPopupBuilder(content, null);
            myPopup = builder
                .setRequestFocus(false)
                .setCancelKeyEnabled(false)
                .setCancelCallback(new Computable<Boolean>() {
                  @Override
                  public Boolean compute() {
                    return myBalloon == null ||
                        myBalloon.isDisposed() ||
                        (!getField().getTextEditor().hasFocus() && !mySkipFocusGain);
                  }
                })
                .setShowShadow(false)
                .setShowBorder(false)
                .createPopup();
            myPopup.getContent().setBorder(null);
            Disposer.register(myPopup, new Disposable() {
              @Override
              public void dispose() {
                ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
                  public void run() {
                    callback.setDone();
                    resetFields();
                    SwingUtilities.invokeLater(new Runnable() {
                      @Override
                      public void run() {
                        ActionToolbarImpl.updateAllToolbarsImmediately();
                      }
                    });
                    if (myActionEvent != null &&
                        myActionEvent.getInputEvent() instanceof MouseEvent) {
                      final Component component = myActionEvent.getInputEvent().getComponent();
                      if (component != null) {
                        final JLabel label = UIUtil.getParentOfType(JLabel.class, component);
                        if (label != null) {
                          label.setIcon(AllIcons.Actions.FindPlain);
                        }
                      }
                    }
                    myActionEvent = null;
                  }
                });
              }
            });
            updatePopupBounds();
            myPopup.show(
                new RelativePoint(getField().getParent(),
                    new Point(0, getField().getParent().getHeight()))
            );

            ActionManager.getInstance().addAnActionListener(new AnActionListener.Adapter() {
              @Override
              public void beforeActionPerformed(
                  AnAction action,
                  DataContext dataContext,
                  AnActionEvent event) {
                if (action instanceof TextComponentEditorAction) {
                  return;
                }
                if (myPopup != null) {
                  myPopup.cancel();
                }
              }
            }, myPopup);
          } else {
            myList.revalidate();
            myList.repaint();
          }
          ListScrollingUtil.ensureSelectionExists(myList);
          if (myList.getModel().getSize() > 0) {
            updatePopupBounds();
          }
        }
      });
    }

    public ActionCallback start() {
      ApplicationManager.getApplication().executeOnPooledThread(this);
      return myDone;
    }

    public ActionCallback cancel() {
      myDone.setRejected();
      return myDone;
    }

    private boolean isCanceled() {
      return myProgressIndicator.isCanceled() || myDone.isRejected();
    }
  }
}
