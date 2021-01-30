package com.lagu.WiPiCar;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;


public class FXController {

    // FXML Injections
    @FXML
    private BorderPane borderPane;
    @FXML
    private AnchorPane bpBottomPane;
    @FXML
    private Button forwardButton;
    @FXML
    private Button rightButton;
    @FXML
    private Button leftButton;
    @FXML
    private Button reverseButton;
    @FXML
    private Button connectButton;
    @FXML
    private Button cameraButton;
    @FXML
    private TextArea windowTerminal;
    @FXML
    private AnchorPane bpCenterPane;
    @FXML
    private ImageView imageView;

    // Non-FXML
    CarControl controlServer;
    FFmpegFXImageDecoder imageServer;

    private
    @FXML
    void cameraButtonAction(ActionEvent event) {
        this.imageServer = new FFmpegFXImageDecoder(windowTerminal, imageView,8000,100,
                "h264", 96,25000000,
                "ultrafast",0);
        Thread imageProcessingThread = new Thread(this.imageServer);
        imageProcessingThread.setDaemon(true);
        imageProcessingThread.start();
        this.controlServer.controlState.set(5);
    }

    @FXML
    void connectButtonAction(ActionEvent event) {
        this.controlServer = new CarControl(8001, 100, windowTerminal);
        Thread controlThread = new Thread(this.controlServer);
        controlThread.setDaemon(true);
        controlThread.start();
        connectButton.setDisable(true);
        this.enableButtons();
    }

    @FXML
    void forwardButtonAction(ActionEvent event) {
        if (this.controlServer.controlState.get() == 0) {
            this.controlServer.controlState.set(1);
        }
    }

    @FXML
    void reverseButtonAction(ActionEvent event) {
        if (this.controlServer.controlState.get() == 0) {
            this.controlServer.controlState.set(3);
        }
    }

    @FXML
    void rightButtonAction(ActionEvent event) {
        if (this.controlServer.controlState.get() == 0) {
            this.controlServer.controlState.set(2);
        }
    }

    @FXML
    void leftButtonAction(ActionEvent event) {
        if (this.controlServer.controlState.get() == 0) {
            this.controlServer.controlState.set(4);
        }
    }

    private void enableButtons() {
        cameraButton.setDisable(false);
        forwardButton.setDisable(false);
        reverseButton.setDisable(false);
        rightButton.setDisable(false);
        leftButton.setDisable(false);
    }
}
