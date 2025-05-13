package rfm.client;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import rfm.client.model.ClientConnection;
import rfm.client.view.PortSelect;
import rfm.client.view.UserInterface;

import java.io.*;

public class Client extends Application {
    private Stage stage;
    private PortSelect portSelect;

    private MacroState macroState = MacroState.STOPPED;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        portSelect = new PortSelect(stage, this::beginConnection);

        this.stage.show();
    }

    private void beginConnection() {
        String ip = portSelect.getIp().isEmpty() ? "localhost" : portSelect.getIp();
        int port = portSelect.getPort() == -1 ? 5000 : portSelect.getPort();
        System.out.println("Connecting to " + ip + ":" + port);

        ClientConnection clientConnection = new ClientConnection(ip, port);

        UserInterface ui = new UserInterface(
            stage,
            () -> {
                macroState = macroState == MacroState.RUNNING ? MacroState.PAUSED : MacroState.RUNNING;
                String command = macroState == MacroState.RUNNING ? "pause" : "unpause";
                clientConnection.sendCommand(command);
            },
            () -> {
                clientConnection.sendCommand("stop");
            },
            mode -> {
                System.out.println("Selected mode: " + mode);
                clientConnection.sendCommand("start " + mode);
            }
        );

        AnimationTimer animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                ByteArrayInputStream screenShareStream = clientConnection.getScreenShareStream();
                if (screenShareStream == null) return;

                Image image = new Image(screenShareStream);
                ui.setScreenShareImage(image);
            }
        };

        animationTimer.start();
    }
}
