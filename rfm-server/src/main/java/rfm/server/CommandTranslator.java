package rfm.server;

public class CommandTranslator {
    public static Command translate(String command) {
        String[] parts = command.split(" ");
        String action = parts[0];
        String target = parts.length > 1 ? parts[1] : null;

        switch (action) {
            case "start":
                if (target == null) {
                    throw new IllegalArgumentException("Target mode is required for start command");
                }

                Mode mode = Mode.valueOf(target.toUpperCase());
                Command startCommand = Command.START;
                startCommand.setMode(mode);
                return startCommand;
            case "stop":
                return Command.STOP;
            case "pause":
                return Command.PAUSE;
            case "unpause":
                return Command.UNPAUSE;
            case "kill":
                return Command.KILL;
            default:
                throw new IllegalArgumentException("Unknown command: " + command);
        }
    }
}
