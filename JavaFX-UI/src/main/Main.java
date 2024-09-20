package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader();
        URL location = getClass().getResource("App.fxml");
        fxmlLoader.setLocation(location);
        Parent root = fxmlLoader.load(location.openStream());

        Scene scene = new Scene(root,1200,700);
        ((AppController)fxmlLoader.getController()).applySkin("default");
        primaryStage.setTitle("Shticell");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
