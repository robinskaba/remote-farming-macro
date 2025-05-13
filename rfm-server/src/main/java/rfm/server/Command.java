package rfm.server;

public enum Command {
    START, STOP, PAUSE, UNPAUSE, KILL;

    private Mode mode;

    Command() {
        this.mode = null;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public Mode getMode() {
        return mode;
    }
}
