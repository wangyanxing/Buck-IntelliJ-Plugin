package com.intellij.plugin.buck.toolwindow;

import com.intellij.codeInsight.lookup.LookupManager;
import com.intellij.execution.Executor;
import com.intellij.execution.ExecutorRegistry;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.actions.ChooseRunConfigurationPopup;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.impl.RunDialog;
import com.intellij.featureStatistics.FeatureUsageTracker;
import com.intellij.icons.AllIcons;
import com.intellij.ide.DataManager;
import com.intellij.ide.actions.ActivateToolWindowAction;
import com.intellij.ide.ui.OptionsTopHitProvider;
import com.intellij.ide.ui.laf.darcula.ui.DarculaTextBorder;
import com.intellij.ide.ui.laf.darcula.ui.DarculaTextFieldUI;
import com.intellij.ide.ui.search.BooleanOptionDescription;
import com.intellij.ide.ui.search.OptionDescription;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.ide.util.gotoByName.GotoActionModel;
import com.intellij.ide.util.gotoByName.GotoClassModel2;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.actionSystem.ex.AnActionListener;
import com.intellij.openapi.actionSystem.impl.ActionToolbarImpl;
import com.intellij.openapi.application.AccessToken;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actions.TextComponentEditorAction;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.keymap.KeymapUtil;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.util.ProgressIndicatorBase;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.ComponentPopupBuilder;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.util.*;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFilePathWrapper;
import com.intellij.openapi.wm.*;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.NameUtil;
import com.intellij.ui.*;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.ui.border.CustomLineBorder;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.OnOffButton;
import com.intellij.ui.components.panels.NonOpaquePanel;
import com.intellij.ui.popup.AbstractPopup;
import com.intellij.ui.popup.PopupPositionManager;
import com.intellij.util.Alarm;
import com.intellij.util.Function;
import com.intellij.util.IconUtil;
import com.intellij.util.ReflectionUtil;
import com.intellij.util.text.Matcher;
import com.intellij.util.ui.EmptyIcon;
import com.intellij.util.ui.JBUI;
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

  private static AtomicBoolean ourShiftIsPressed = new AtomicBoolean(false);
  private static AtomicBoolean showAll = new AtomicBoolean(false);

  private MySearchTextField myPopupField;
  private HistoryItem myHistoryItem;
  private int myHistoryIndex = 0;
  private JBList myList;
  private volatile GotoClassModel2 myClassModel;
  private boolean mySkipFocusGain = false;
  private Editor myEditor;
  private Component myFocusComponent;
  private JBPopup myPopup;
  private CalcThread myCalcThread;
  private MyListRenderer myRenderer;
  private volatile JBPopup myBalloon;
  private Alarm myAlarm = new Alarm(Alarm.ThreadToUse.SWING_THREAD, ApplicationManager.getApplication());
  private volatile ActionCallback myCurrentWorker = ActionCallback.DONE;
  private int myPopupActualWidth;
  private Map<String, String> myConfigurables = new HashMap<String, String>();
  private FileEditor myFileEditor;
  private PsiFile myFile;
  private Component myFocusOwner;
  private Component myContextComponent;
  private AnActionEvent myActionEvent;

  public ChooseTargetAction() {
    super("Choose target", "Choose build target", AllIcons.Actions.Preview);

    updateComponents();
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        onFocusLost();
      }
    });
  }

  private static boolean isActionValue(Object o) {
    return o instanceof GotoActionModel.ActionWrapper || o instanceof AnAction;
  }

  private static boolean isSetting(Object o) {
    return o instanceof OptionDescription;
  }

  private static boolean isRunConfiguration(Object o) {
    return o instanceof ChooseRunConfigurationPopup.ItemWrapper;
  }

  private static boolean isVirtualFile(Object o) {
    return o instanceof VirtualFile;
  }

  private static Font getTitleFont() {
    return UIUtil.getLabelFont().deriveFont(UIUtil.getFontSize(UIUtil.FontSize.SMALL));
  }

  @Override
  public void actionPerformed(AnActionEvent e) {
    if (myBalloon != null && myBalloon.isVisible()) {
      showAll.set(!showAll.get());
      rebuildList(myPopupField.getText());
      return;
    }
    myCurrentWorker = ActionCallback.DONE;
    if (e != null) {
      myEditor = e.getData(CommonDataKeys.EDITOR);
      myFileEditor = e.getData(PlatformDataKeys.FILE_EDITOR);
      myFile = e.getData(CommonDataKeys.PSI_FILE);
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
    registerDataProvider(panel, project);
    final RelativePoint showPoint;
    if (parent != null) {
      showPoint = new RelativePoint(parent, new Point((parent.getSize().width - panel.getPreferredSize().width) / 2,
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

  private void registerDataProvider(JPanel panel, final Project project) {
    DataManager.registerDataProvider(panel, new DataProvider() {
      @Nullable
      @Override
      public Object getData(@NonNls String dataId) {
        final Object value = myList.getSelectedValue();
        if (CommonDataKeys.PSI_ELEMENT.is(dataId) && value instanceof PsiElement) {
          return value;
        } else if (CommonDataKeys.VIRTUAL_FILE.is(dataId) && value instanceof VirtualFile) {
          return value;
        } else if (CommonDataKeys.NAVIGATABLE.is(dataId)) {
          if (value instanceof Navigatable) return value;
          if (value instanceof ChooseRunConfigurationPopup.ItemWrapper) {
            final Object config = ((ChooseRunConfigurationPopup.ItemWrapper) value).getValue();
            if (config instanceof RunnerAndConfigurationSettings) {
              return new Navigatable() {
                @Override
                public void navigate(boolean requestFocus) {
                  RunDialog.editConfiguration(project, (RunnerAndConfigurationSettings) config,
                      "Edit Configuration", getExecutor());
                }

                @Override
                public boolean canNavigate() {
                  return true;
                }

                @Override
                public boolean canNavigateToSource() {
                  return true;
                }
              };
            }
          }
        }
//                else if (PlatformDataKeys.SEARCH_INPUT_TEXT.is(dataId)) {
//                    return myPopupField == null ? null : myPopupField.getText();
//                }
        return null;
      }
    });
  }

  public Executor getExecutor() {
    return ourShiftIsPressed.get() ? DefaultRunExecutor.getRunExecutorInstance()
        : ExecutorRegistry.getInstance().getExecutorById(ToolWindowId.DEBUG);
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
        sz = new Dimension(Math.min(POPUP_MAX_WIDTH, Math.max(getField().getWidth(), sz.width + extraWidth)), Math.min(POPUP_MAX_WIDTH, sz.height + extraHeight));
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
    Rectangle r = new Rectangle(myRelativeOnScreen.x, myRelativeOnScreen.y + myRelativeTo.getHeight(), d.width, d.height);

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
            myClassModel = null;
            myConfigurables.clear();
            myFocusComponent = null;
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
            myFileEditor = null;
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
          doNavigate(index);
        }
      }
    }.registerCustomShortcutSet(CustomShortcutSet.fromString("ENTER", "shift ENTER"), editor, balloon);
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
        //titleIndex = new TitleIndexes();
        editor.setColumns(SEARCH_FIELD_COLUMNS);
        myFocusComponent = e.getOppositeComponent();
        //noinspection SSBasedInspection
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

  private static String getSettingText(OptionDescription value) {
    String hit = value.getHit();
    if (hit == null) {
      hit = value.getOption();
    }
    hit = StringUtil.unescapeXml(hit);
    if (hit.length() > 60) {
      hit = hit.substring(0, 60) + "...";
    }
    hit = hit.replace("  ", " "); //avoid extra spaces from mnemonics and xml conversion
    String text = hit.trim();
    if (text.endsWith(":")) {
      text = text.substring(0, text.length() - 1);
    }
    return text;
  }

  private boolean isMoreItem(int index) {
    final SearchListModel model = getModel();
    return index == model.moreIndex.classes ||
        index == model.moreIndex.files ||
        index == model.moreIndex.settings ||
        index == model.moreIndex.actions ||
        index == model.moreIndex.symbols ||
        index == model.moreIndex.runConfigurations;
  }

  private void rebuildList(final String pattern) {
    assert EventQueue.isDispatchThread() : "Must be EDT";
    if (myCalcThread != null && !myCurrentWorker.isProcessed()) {
      myCurrentWorker = myCalcThread.cancel();
    }
    if (myCalcThread != null && !myCalcThread.isCanceled()) {
      myCalcThread.cancel();
    }
    final Project project = CommonDataKeys.PROJECT.getData(DataManager.getInstance().getDataContext(getField().getTextEditor()));

    assert project != null;
    myRenderer.myProject = project;
    final Runnable run = new Runnable() {
      @Override
      public void run() {
        myCalcThread = new CalcThread(project, pattern, false);
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
        return new Dimension(Math.max(myBalloon.getSize().width, Math.min(size.width - 2, POPUP_MAX_WIDTH)),
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
          //noinspection SSBasedInspection
          SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
              myList.setSelectedIndex(i);
              doNavigate(i);
            }
          });
        }
      }
    });
  }

  private void doNavigate(final int index) {
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
    //noinspection SSBasedInspection
    UIUtil.invokeLaterIfNeeded(new Runnable() {
      @Override
      public void run() {
        try {
          if (myCalcThread != null) {
            myCalcThread.cancel();
            //myCalcThread = null;
          }
          myAlarm.cancelAllRequests();
          if (myBalloon != null && !myBalloon.isDisposed() && myPopup != null && !myPopup.isDisposed()) {
            myBalloon.cancel();
            myPopup.cancel();
          }

          //noinspection SSBasedInspection
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

  private static class MySearchTextField extends SearchTextField implements DataProvider, Disposable {
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

  static class MoreIndex {
    volatile int classes = -1;
    volatile int files = -1;
    volatile int actions = -1;
    volatile int settings = -1;
    volatile int symbols = -1;
    volatile int runConfigurations = -1;
    volatile int structure = -1;

    public void shift(int index, int shift) {
      if (runConfigurations >= index) runConfigurations += shift;
      if (classes >= index) classes += shift;
      if (files >= index) files += shift;
      if (symbols >= index) symbols += shift;
      if (actions >= index) actions += shift;
      if (settings >= index) settings += shift;
      if (structure >= index) structure += shift;
    }
  }

  static class TitleIndex {
    volatile int topHit = -1;
    volatile int recentFiles = -1;
    volatile int runConfigurations = -1;
    volatile int classes = -1;
    volatile int structure = -1;
    volatile int files = -1;
    volatile int actions = -1;
    volatile int settings = -1;
    volatile int toolWindows = -1;
    volatile int symbols = -1;

    final String gotoClassTitle;
    final String gotoFileTitle;
    final String gotoActionTitle;
    final String gotoSettingsTitle;
    final String gotoRecentFilesTitle;
    final String gotoRunConfigurationsTitle;
    final String gotoSymbolTitle;
    final String gotoStructureTitle;
    static final String toolWindowsTitle = "Tool Windows";

    TitleIndex() {
      String gotoClass = KeymapUtil.getFirstKeyboardShortcutText(ActionManager.getInstance().getAction("GotoClass"));
      gotoClassTitle = StringUtil.isEmpty(gotoClass) ? "Classes" : "Classes (" + gotoClass + ")";
      String gotoFile = KeymapUtil.getFirstKeyboardShortcutText(ActionManager.getInstance().getAction("GotoFile"));
      gotoFileTitle = StringUtil.isEmpty(gotoFile) ? "Files" : "Files (" + gotoFile + ")";
      String gotoAction = KeymapUtil.getFirstKeyboardShortcutText(ActionManager.getInstance().getAction("GotoAction"));
      gotoActionTitle = StringUtil.isEmpty(gotoAction) ? "Actions" : "Actions (" + gotoAction + ")";
      String gotoSettings = KeymapUtil.getFirstKeyboardShortcutText(ActionManager.getInstance().getAction("ShowSettings"));
      gotoSettingsTitle = StringUtil.isEmpty(gotoAction) ? ShowSettingsUtil.getSettingsMenuName() : ShowSettingsUtil.getSettingsMenuName() + " (" + gotoSettings + ")";
      String gotoRecentFiles = KeymapUtil.getFirstKeyboardShortcutText(ActionManager.getInstance().getAction("RecentFiles"));
      gotoRecentFilesTitle = StringUtil.isEmpty(gotoRecentFiles) ? "Recent Files" : "Recent Files (" + gotoRecentFiles + ")";
      String gotoSymbol = KeymapUtil.getFirstKeyboardShortcutText(ActionManager.getInstance().getAction("GotoSymbol"));
      gotoSymbolTitle = StringUtil.isEmpty(gotoSymbol) ? "Symbols" : "Symbols (" + gotoSymbol + ")";
      String gotoRunConfiguration = KeymapUtil.getFirstKeyboardShortcutText(ActionManager.getInstance().getAction("ChooseDebugConfiguration"));
      if (StringUtil.isEmpty(gotoRunConfiguration)) {
        gotoRunConfiguration = KeymapUtil.getFirstKeyboardShortcutText(ActionManager.getInstance().getAction("ChooseRunConfiguration"));
      }
      gotoRunConfigurationsTitle = StringUtil.isEmpty(gotoRunConfiguration) ? "Run Configurations" : "Run Configurations (" + gotoRunConfiguration + ")";
      String gotoStructure = KeymapUtil.getFirstKeyboardShortcutText(ActionManager.getInstance().getAction("FileStructurePopup"));
      gotoStructureTitle = StringUtil.isEmpty(gotoStructure) ? "File Structure" : "File Structure (" + gotoStructure + ")";
    }

    String getTitle(int index) {
      if (index == topHit) return index == 0 ? "Top Hit" : "Top Hits";
      if (index == recentFiles) return gotoRecentFilesTitle;
      if (index == structure) return gotoStructureTitle;
      if (index == runConfigurations) return gotoRunConfigurationsTitle;
      if (index == classes) return gotoClassTitle;
      if (index == files) return gotoFileTitle;
      if (index == toolWindows) return toolWindowsTitle;
      if (index == actions) return gotoActionTitle;
      if (index == settings) return gotoSettingsTitle;
      if (index == symbols) return gotoSymbolTitle;
      return null;
    }

    public void clear() {
      topHit = -1;
      runConfigurations = -1;
      recentFiles = -1;
      classes = -1;
      files = -1;
      structure = -1;
      actions = -1;
      settings = -1;
      toolWindows = -1;
    }

    public void shift(int index, int shift) {
      if (toolWindows != -1 && toolWindows > index) toolWindows += shift;
      if (settings != -1 && settings > index) settings += shift;
      if (actions != -1 && actions > index) actions += shift;
      if (files != -1 && files > index) files += shift;
      if (structure != -1 && structure > index) structure += shift;
      if (classes != -1 && classes > index) classes += shift;
      if (runConfigurations != -1 && runConfigurations > index) runConfigurations += shift;
      if (symbols != -1 && symbols > index) symbols += shift;
    }
  }

  @SuppressWarnings("unchecked")
  private static class SearchListModel extends DefaultListModel {
    @SuppressWarnings("UseOfObsoleteCollectionType")
    Vector myDelegate;

    volatile TitleIndex titleIndex = new TitleIndex();
    volatile MoreIndex moreIndex = new MoreIndex();

    private SearchListModel() {
      super();
      myDelegate = ReflectionUtil.getField(DefaultListModel.class, this, Vector.class, "delegate");
    }

    int next(int index) {
      int[] all = getAll();
      Arrays.sort(all);
      for (int next : all) {
        if (next > index) return next;
      }
      return 0;
    }

    int[] getAll() {
      return new int[]{
          titleIndex.topHit,
          titleIndex.recentFiles,
          titleIndex.structure,
          titleIndex.runConfigurations,
          titleIndex.classes,
          titleIndex.files,
          titleIndex.actions,
          titleIndex.settings,
          titleIndex.toolWindows,
          titleIndex.symbols,
          moreIndex.classes,
          moreIndex.actions,
          moreIndex.files,
          moreIndex.settings,
          moreIndex.symbols,
          moreIndex.runConfigurations,
          moreIndex.structure
      };
    }

    int prev(int index) {
      int[] all = getAll();
      Arrays.sort(all);
      for (int i = all.length - 1; i >= 0; i--) {
        if (all[i] != -1 && all[i] < index) return all[i];
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
    //SearchEverywherePsiRenderer myFileRenderer = new SearchEverywherePsiRenderer(myList);
    @SuppressWarnings("unchecked")
    ListCellRenderer myActionsRenderer = new GotoActionModel.GotoActionListCellRenderer(Function.TO_STRING);

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

    public void setLocationString(String locationString) {
      myLocationString = locationString;
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
      Component cmp;
      PsiElement file;
      myLocationString = null;
      String pattern = "*" + myPopupField.getText();
      Matcher matcher = NameUtil.buildMatcher(pattern, 0, true, true);
//            if (isMoreItem(index)) {
//                cmp = More.get(isSelected);
//            } else if (value instanceof VirtualFile
//                    && myProject != null
//                    && ((((VirtualFile)value).isDirectory() && (file = PsiManager.getInstance(myProject).findDirectory((VirtualFile)value)) != null )
//                    || (file = PsiManager.getInstance(myProject).findFile((VirtualFile)value)) != null)) {
//                myFileRenderer.setPatternMatcher(matcher);
//                cmp = myFileRenderer.getListCellRendererComponent(list, file, index, isSelected, cellHasFocus);
//            } else if (value instanceof PsiElement) {
//                myFileRenderer.setPatternMatcher(matcher);
//                cmp = myFileRenderer.getListCellRendererComponent(list, value, index, isSelected, isSelected);
//            } else if (value instanceof GotoActionModel.ActionWrapper) {
//                cmp = myActionsRenderer.getListCellRendererComponent(list, new GotoActionModel.MatchedValue(((GotoActionModel.ActionWrapper)value), pattern), index, isSelected, isSelected);
//            } else {
      cmp = super.getListCellRendererComponent(list, value, index, isSelected, isSelected);
      final JPanel p = new JPanel(new BorderLayout());
      p.setBackground(UIUtil.getListBackground(isSelected));
      p.add(cmp, BorderLayout.CENTER);
      cmp = p;
      //}

      if (myLocationString != null || value instanceof BooleanOptionDescription) {
        final JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIUtil.getListBackground(isSelected));
        panel.add(cmp, BorderLayout.CENTER);
        final Component rightComponent;
        if (value instanceof BooleanOptionDescription) {
          final OnOffButton button = new OnOffButton();
          button.setSelected(((BooleanOptionDescription) value).isOptionEnabled());
          rightComponent = button;
        } else {
          rightComponent = myLocation.getListCellRendererComponent(list, value, index, isSelected, isSelected);
        }
        panel.add(rightComponent, BorderLayout.EAST);
        cmp = panel;
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
        //schedulePopupUpdate();
      }
      return myMainPanel;
    }

    @Override
    protected void customizeCellRenderer(JList list, Object value, int index, boolean selected, boolean hasFocus) {
      setPaintFocusBorder(false);
      setIcon(EmptyIcon.ICON_16);
      AccessToken token = ApplicationManager.getApplication().acquireReadActionLock();
      try {
        if (value instanceof PsiElement) {
          String name = myClassModel.getElementName(value);
          assert name != null;
          append(name);
        } else if (value instanceof ChooseRunConfigurationPopup.ItemWrapper) {
          final ChooseRunConfigurationPopup.ItemWrapper wrapper = (ChooseRunConfigurationPopup.ItemWrapper) value;
          append(wrapper.getText());
          setIcon(wrapper.getIcon());
          setLocationString(ourShiftIsPressed.get() ? "Run" : "Debug");
          myLocationIcon = ourShiftIsPressed.get() ? AllIcons.Toolwindows.ToolWindowRun : AllIcons.Toolwindows.ToolWindowDebugger;
        } else if (isVirtualFile(value)) {
          final VirtualFile file = (VirtualFile) value;
          if (file instanceof VirtualFilePathWrapper) {
            append(((VirtualFilePathWrapper) file).getPresentablePath());
          } else {
            append(file.getName());
          }
          setIcon(IconUtil.getIcon(file, Iconable.ICON_FLAG_READ_STATUS, myProject));
        } else if (isActionValue(value)) {
          final GotoActionModel.ActionWrapper actionWithParentGroup = value instanceof GotoActionModel.ActionWrapper ? (GotoActionModel.ActionWrapper) value : null;
          final AnAction anAction = actionWithParentGroup == null ? (AnAction) value : actionWithParentGroup.getAction();
          final Presentation templatePresentation = anAction.getTemplatePresentation();
          Icon icon = templatePresentation.getIcon();
          if (anAction instanceof ActivateToolWindowAction) {
            final String id = ((ActivateToolWindowAction) anAction).getToolWindowId();
            ToolWindow toolWindow = ToolWindowManager.getInstance(myProject).getToolWindow(id);
            if (toolWindow != null) {
              icon = toolWindow.getIcon();
            }
          }

          append(templatePresentation.getText());
          if (actionWithParentGroup != null) {
            final String groupName = actionWithParentGroup.getGroupName();
            if (!StringUtil.isEmpty(groupName)) {
              setLocationString(groupName);
            }
          }

          final String groupName = actionWithParentGroup == null ? null : actionWithParentGroup.getGroupName();
          if (!StringUtil.isEmpty(groupName)) {
            setLocationString(groupName);
          }
          if (icon != null && icon.getIconWidth() <= 16 && icon.getIconHeight() <= 16) {
            setIcon(IconUtil.toSize(icon, 16, 16));
          }
        } else if (isSetting(value)) {
          String text = getSettingText((OptionDescription) value);
          append(text);
          final String id = ((OptionDescription) value).getConfigurableId();
          final String name = myConfigurables.get(id);
          if (name != null) {
            setLocationString(name);
          }
        } else if (value instanceof OptionsTopHitProvider) {
          append("#" + ((OptionsTopHitProvider) value).getId());
        } else {
          ItemPresentation presentation = null;
          if (value instanceof ItemPresentation) {
            presentation = (ItemPresentation) value;
          } else if (value instanceof NavigationItem) {
            presentation = ((NavigationItem) value).getPresentation();
          }
          if (presentation != null) {
            final String text = presentation.getPresentableText();
            append(text == null ? value.toString() : text);
            final String location = presentation.getLocationString();
            if (!StringUtil.isEmpty(location)) {
              setLocationString(location);
            }
            Icon icon = presentation.getIcon(false);
            if (icon != null) setIcon(icon);
          }
        }
      } finally {
        token.finish();
      }
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
    private final Project project;
    private final String pattern;
    private final ProgressIndicator myProgressIndicator = new ProgressIndicatorBase();
    private final ActionCallback myDone = new ActionCallback();
    private final SearchListModel myListModel;
    private final ArrayList<VirtualFile> myAlreadyAddedFiles = new ArrayList<VirtualFile>();
    private final ArrayList<AnAction> myAlreadyAddedActions = new ArrayList<AnAction>();

    public CalcThread(Project project, String pattern, boolean reuseModel) {
      this.project = project;
      this.pattern = pattern;
      myListModel = reuseModel ? (SearchListModel) myList.getModel() : new SearchListModel();
    }

    @Override
    public void run() {
      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          myList.getEmptyText().setText("Searching...");

          if (myList.getModel() instanceof SearchListModel) {
            //noinspection unchecked
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
    }

    protected void check() {
      myProgressIndicator.checkCanceled();
      if (myDone.isRejected()) throw new ProcessCanceledException();
      if (myBalloon == null || myBalloon.isDisposed()) throw new ProcessCanceledException();
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
            final ActionCallback callback = ListDelegationUtil.installKeyboardDelegation(getField().getTextEditor(), myList);
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
                    return myBalloon == null || myBalloon.isDisposed() || (!getField().getTextEditor().hasFocus() && !mySkipFocusGain);
                  }
                })
                .setShowShadow(false)
                .setShowBorder(false)
                .createPopup();
            //myPopup.setMinimumSize(new Dimension(myBalloon.getSize().width, 30));
            myPopup.getContent().setBorder(null);
            Disposer.register(myPopup, new Disposable() {
              @Override
              public void dispose() {
                ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
                  public void run() {
                    callback.setDone();
                    resetFields();
                    //noinspection SSBasedInspection
                    SwingUtilities.invokeLater(new Runnable() {
                      @Override
                      public void run() {
                        ActionToolbarImpl.updateAllToolbarsImmediately();
                      }
                    });
                    if (myActionEvent != null && myActionEvent.getInputEvent() instanceof MouseEvent) {
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
            myPopup.show(new RelativePoint(getField().getParent(), new Point(0, getField().getParent().getHeight())));

            ActionManager.getInstance().addAnActionListener(new AnActionListener.Adapter() {
              @Override
              public void beforeActionPerformed(AnAction action, DataContext dataContext, AnActionEvent event) {
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

  static class More extends JPanel {
    static final More instance = new More();
    final JLabel label = new JLabel("    ... more   ");

    private More() {
      super(new BorderLayout());
      add(label, BorderLayout.CENTER);
    }

    static More get(boolean isSelected) {
      instance.setBackground(UIUtil.getListBackground(isSelected));
      instance.label.setForeground(UIUtil.getLabelDisabledForeground());
      instance.label.setFont(getTitleFont());
      instance.label.setBackground(UIUtil.getListBackground(isSelected));
      return instance;
    }
  }
}
