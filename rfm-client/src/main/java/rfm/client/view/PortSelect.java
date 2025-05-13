package rfm.client.view;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PortSelect {
    private static final int width = 300;
    private static final int height = 400;
    private final Runnable onConnect;

    private VBox root;
    private TextField ipField;
    private TextField portField;

    public PortSelect(Stage stage, Runnable onConnect) {
        this.onConnect = onConnect;

        stage.setTitle("RFM Client - Select port");
        stage.setResizable(false);
        stage.setWidth(width);
        stage.setHeight(height);

        setup();

        Scene scene = new Scene(root, width, height);
        stage.setScene(scene);
        stage.sizeToScene();
    }

    private void setup() {
        root = new VBox();
        root.getStylesheets().add(getClass().getResource("/port_select.css").toExternalForm());
        root.getStyleClass().add("root");

        ipField = new TextField();
        portField = new TextField();
        Button connectButton = new Button("Connect");

        ipField.setPromptText("Enter IP address");
        portField.setPromptText("Enter port number");

        ipField.getStyleClass().add("field");
        portField.getStyleClass().add("field");
        connectButton.getStyleClass().add("submit");
        connectButton.setPrefWidth(width);

        connectButton.setOnAction(_ -> onConnect.run());

        Label header = new Label("Connect to server");
        Label ipLabel = new Label("IP Address");
        Label portLabel = new Label("Port Number");

        header.getStyleClass().add("header");
        ipLabel.getStyleClass().add("label");
        portLabel.getStyleClass().add("label");

        root.getChildren().addAll(
            header, ipLabel, ipField, portLabel, portField, connectButton
        );
    }

    public String getIp() {
        return ipField.getText();
    }

    public int getPort() {
        try {
            return Integer.parseInt(portField.getText());
        } catch (NumberFormatException e) {
            System.err.println("No port number provided");
            return -1;
        }
    }
}
