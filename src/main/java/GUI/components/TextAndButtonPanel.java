package GUI.components;

import GUI.service.DatabaseService;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TextAndButtonPanel {
    String date;
    double hours;
    TextArea activity;
    Pane root;
    TextFlow textFlow;
    Button button;
    VBox mainContainer;
    int ID;

    /**
     * Konstruktor vytvara riadok s textoma buttonom na zaklade vstupny parametrov
     */
    public TextAndButtonPanel(LocalDate date, double hours, TextArea activity, VBox mainContainer, int ID) {
        this.date = date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        this.hours = hours;
        this.activity = activity;
        this.mainContainer = mainContainer;
        this.ID = ID;
        crteateLineWithTextAndButton(mainContainer);
    }

    /**
     * Vytvara cely riadok textu + delete button, prisposoby vysku root podla textu a vkada do VBox
     */
    private void crteateLineWithTextAndButton(VBox mainContainer) {
        root = new Pane();
        displayText(date, 20, 90);
        displayText(String.valueOf(hours), 120, 50);
        displayText(activity.getText(), 180, 400);

        createDeleteButton();

        assert textFlow != null;
        textFlow.heightProperty().addListener((obs, oldVal, newVal) -> {
            root.setPrefHeight(newVal.doubleValue() + 15);
        });

        mainContainer.getChildren().add(root);
    }

    /**
     * Vytvara jednotlive casti textu zobrazujuce sa pre uzivatela na zaklade vstupnych dat.
     */
    private void displayText(String displayText, int x, int prefWidth) {
        textFlow = new TextFlow(new Text(displayText));
        textFlow.setLayoutX(x);
        textFlow.setLayoutY(10);
        textFlow.setPrefWidth(prefWidth);

        root.getChildren().add(textFlow);
    }

    /**
     * Vytvara delete button
     */
    private void createDeleteButton() {
        button = new Button("X");
        button.setLayoutX(595);
        button.setLayoutY(10);
        button.setPrefSize(25, 25);

        button.setOnAction(f -> {
            mainContainer.getChildren().remove(root);
            DatabaseService.deleteData(ID, date);
        });

        root.getChildren().add(button);
    }
}
