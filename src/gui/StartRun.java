package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import main.Common;

import java.io.IOException;


public class StartRun extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Common.establishConnection();
        Parent root = FXMLLoader.load(getClass().getResource("View.fxml"));
        primaryStage.setMinWidth(625);
        primaryStage.setMinHeight(500);
        Image image = new Image("file:icon.png");
        primaryStage.getIcons().add(image);
        primaryStage.setTitle("Self-driving management system ");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}
