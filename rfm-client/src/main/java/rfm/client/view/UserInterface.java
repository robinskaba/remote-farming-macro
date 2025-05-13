package rfm.client.view;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import rfm.client.MacroState;
import rfm.client.Modes;

import java.util.function.Consumer;

import static rfm.client.MacroState.*;

public class UserInterface {
    private static final int width = 350;
    private static final int height = 375;

    private final Consumer<Modes> onSelectMode;
    private final Runnable onExit;
    private final Runnable onPause;

    private final VBox root;

    private ImageView screenShareView;
    private Button selectedModeButton;
    private Button mainButton;

    private MacroState macroState = MacroState.STOPPED;

    public UserInterface(Stage stage, Runnable onPause, Runnable onExit, Consumer<Modes> onSelectMode) {
        this.onPause = onPause;
        this.onExit = onExit;
        this.onSelectMode = onSelectMode;

        root = new VBox();
        root.getStylesheets().add(getClass().getResource("/controls.css").toExternalForm());
        root.getStyleClass().add("root");

        setup();
        adjustMainButtonToMacroState();

        Scene scene = new Scene(root, width, height);
        stage.setScene(scene); // Set the scene first

        stage.setTitle("RFM Client");
        stage.setResizable(false);
        stage.setWidth(width);
        stage.setHeight(height);
        stage.sizeToScene();
    }

    private void setup() {
        screenShare();
        controls();
    }

    private void screenShare() {
        Image placeholderImg = new Image(getClass().getResourceAsStream("/image_placeholder.jpg"));

        screenShareView = new ImageView(placeholderImg);
        screenShareView.setFitWidth(width);
        screenShareView.setFitHeight((double) height / 2);

        root.getChildren().add(screenShareView);
    }

    private void controls() {
        double spacing = 5;

        HBox row = new HBox();
        row.setSpacing(spacing);
        row.setPadding(new Insets(spacing));

        VBox smallButtons = new VBox();
        smallButtons.setSpacing(spacing);

        Button stopButton = new Button("Stop");
        stopButton.setOnAction(e -> {
            macroState = STOPPED;
            selectMode(null, null);
            adjustMainButtonToMacroState();

            onExit.run();
        });

        GridPane modes = new GridPane();
        modes.setHgap(spacing);
        modes.setVgap(spacing);

        Button wartMode = new Button("Wart");
        Button melonMode = new Button("Melon");
        Button sugarMode = new Button("Sugar");
        Button cactiMode = new Button("Cacti");

        wartMode.setOnAction(e -> selectMode(wartMode, Modes.WART));
        melonMode.setOnAction(e -> selectMode(melonMode, Modes.MELON));
        sugarMode.setOnAction(e -> selectMode(sugarMode, Modes.SUGAR));
        cactiMode.setOnAction(e -> selectMode(cactiMode, Modes.CACTI));

        modes.add(wartMode, 0, 0);
        modes.add(melonMode, 1, 0);
        modes.add(sugarMode, 0, 1);
        modes.add(cactiMode, 1, 1);

        mainButton = new Button("ON/OFF");
        mainButton.setOnAction(e -> {
            toggleState();
            adjustMainButtonToMacroState();

            onPause.run();
        });

        // widths
        mainButton.prefWidthProperty().bind(row.widthProperty().multiply(0.5));
        mainButton.prefHeightProperty().bind(mainButton.prefWidthProperty()); // Square

        mainButton.prefWidthProperty().bind(row.widthProperty().multiply(0.5));
        mainButton.prefHeightProperty().bind(mainButton.prefWidthProperty()); // square

        stopButton.prefWidthProperty().bind(row.widthProperty().multiply(0.5));
        wartMode.prefWidthProperty().bind(row.widthProperty().multiply(0.25));
        melonMode.prefWidthProperty().bind(row.widthProperty().multiply(0.25));
        sugarMode.prefWidthProperty().bind(row.widthProperty().multiply(0.25));
        cactiMode.prefWidthProperty().bind(row.widthProperty().multiply(0.25));

        // Match VBox height to ON/OFF button
        smallButtons.prefHeightProperty().bind(mainButton.heightProperty());

        // Divide height between buttons in VBox
        stopButton.prefHeightProperty().bind(smallButtons.heightProperty().multiply(0.33));
        modes.prefHeightProperty().bind(smallButtons.heightProperty().multiply(0.67));

        // Make grid buttons equal height within grid
        wartMode.prefHeightProperty().bind(modes.heightProperty().multiply(0.5));
        melonMode.prefHeightProperty().bind(modes.heightProperty().multiply(0.5));
        sugarMode.prefHeightProperty().bind(modes.heightProperty().multiply(0.5));
        cactiMode.prefHeightProperty().bind(modes.heightProperty().multiply(0.5));

        // put in layout
        smallButtons.getChildren().add(stopButton);
        smallButtons.getChildren().add(modes);
        row.getChildren().add(smallButtons);
        row.getChildren().add(mainButton);

        root.getChildren().add(row);
    }

    private void selectMode(Button modeButton, Modes mode) {
        if (selectedModeButton == modeButton) return;

        if (selectedModeButton != null) {
            selectedModeButton.getStyleClass().remove("selected");
        }

        selectedModeButton = modeButton;

        if (modeButton == null) return;

        selectedModeButton.getStyleClass().add("selected");

        macroState = RUNNING;
        adjustMainButtonToMacroState();

        onSelectMode.accept(mode);
    }

    private void toggleState() {
        macroState = switch (macroState) {
            case PAUSED -> RUNNING;
            case RUNNING -> PAUSED;
            default -> STOPPED;
        };
    }

    private void adjustMainButtonToMacroState() {
        mainButton.getStyleClass().remove("selected");
        mainButton.getStyleClass().remove("paused");

        switch (macroState) {
            case STOPPED -> {
                mainButton.setText("OFF");
            }
            case PAUSED -> {
                mainButton.setText("PAUSED");
                mainButton.getStyleClass().add("paused");
            }
            case RUNNING -> {
                mainButton.setText("RUNNING");
                mainButton.getStyleClass().add("selected");
            }
        }
    }

    public void setScreenShareImage(Image image) {
        screenShareView.setImage(image);
    }
}
