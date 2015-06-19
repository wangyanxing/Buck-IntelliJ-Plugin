
/**
 * Created by longma on 6/18/15.
 */
class BuckCommandProcessMonitor extends Thread{
    private String _commandType;
    private boolean _shouldStop;
    private BuckCommandUtils.ProcessStatus _processStatus;
    private String _strStatus;

    public BuckCommandProcessMonitor(String commandType){
        this._commandType = commandType;
        this._shouldStop = false;
        this._processStatus = BuckCommandUtils.ProcessStatus.NONE;
        this._strStatus = "None";
    }

    public void run() {
        System.out.println("Monitor Started");
        int currentTime = 0;
        for (int i = 0; i < 10; i++) {
            currentTime = i * 5;
            if (currentTime > 0) {
                if (_shouldStop) {
                    break;
                }
                System.out.println(_commandType + " still in process... " +
                        currentTime + " seconds passed (" +
                        _strStatus + ")");
            }
            try {
                sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopBoadcast() {
        this._shouldStop = true;
        System.out.println("Monitor Stopped ("  +
                _strStatus + ")");
    }

    public void updateStatus(BuckCommandUtils.ProcessStatus status){
        this._processStatus = status;
        switch (status) {
            case NONE:
                _strStatus = "None";
                break;
            case STARTED:
                _strStatus = "Started";
                break;
            case PARSING_BUCK_FILES_FINISHED:
                _strStatus = "Parsing Buck Files Finished";
                break;
            case BUILDING_FINISHED:
                _strStatus = "Building Finished";
                break;
            case INSTALLING_FINISHED:
                _strStatus = "Installing Finished";
                break;
            default:
                _strStatus = "None";
                break;
        }
    }
}