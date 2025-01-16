package GUI;

import GUI.eventHandlers.Controller;
import javafx.application.Application;
import javafx.stage.Stage;

public class MonthlyTimesheet extends Application {
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {
        new Controller();
    }
}