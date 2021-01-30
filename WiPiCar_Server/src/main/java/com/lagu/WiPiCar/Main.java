package com.lagu.WiPiCar;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;


public class Main extends Application {
    // resolutions = 1280x720, 960×720, 640x480
    static final int WIDTH = 640;

    static final int HEIGHT = 480;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/WiPiCar.fxml"));
        BorderPane borderPane = fxmlLoader.load();
        AnchorPane bpCenterPane = (AnchorPane) borderPane.getCenter();

        ImageView imageView = (ImageView) bpCenterPane.getChildren().get(0);
        imageView.fitHeightProperty().bind(borderPane.heightProperty().divide(WIDTH).multiply(HEIGHT));
        imageView.fitWidthProperty().bind(borderPane.widthProperty());
        borderPane.setCenter(imageView);

        Scene scene = new Scene(borderPane);
        primaryStage.setTitle("WiPiCar - Server");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}