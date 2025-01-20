package GUI;

import GUI.eventHandlers.Controller;
import javafx.application.Application;
import javafx.stage.Stage;

public class MonthlyTimesheet extends Application {
    public static void main(String[] args) {
        launch();
    }

    // Pri pridani prvej cinnosti v mesiaci (vytvorenie prvej tabulky) mierne pomala reakcia.
    // Pravdepodobne kvoli zistovaniu sviatkov a vikendov.
    // Je potrebne zistit ci je mozne znizit pocet operacii alebo inym sposobom zrychlit proces.

    @Override
    public void start(Stage stage) {
        new Controller();
    }
}