package gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        AssemblerView root = new AssemblerView();
        Scene scene = new Scene(root, 1200, 700);
        primaryStage.setTitle("Motorola 6800 Assembler & Simulator");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
