module rfm.client.rfmclient {
    requires javafx.controls;
    requires javafx.fxml;


    opens rfm.client to javafx.fxml;
    exports rfm.client;
    exports rfm.client.view;
    opens rfm.client.view to javafx.fxml;
}