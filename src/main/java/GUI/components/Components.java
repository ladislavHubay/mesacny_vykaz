package GUI.components;

import GUI.eventHandlers.RecordBuilderForDB;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Components {
    public TextArea textAreaActivity;
    public TextArea textAreaHours;
    public TextArea textAreaDate;
    public boolean vacation;
    public boolean doctor;
    public boolean pn;
    public Button button;

    /**
     * Konstruktor nastavuje automaticke aktivity
     */
    public Components() {
        vacation = false;
        doctor = false;
        pn = false;
    }

    /**
     * Nastavuje konkretne hodnoty pre textove polia urcene pre vstup od uzivatela.
     */
    public void createTextAreas(Pane root){
        textAreaDate = createTextArea(90, 35, 20, 20, LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        textAreaHours = createTextArea(50, 35, 120, 20, "cas");
        textAreaActivity = createTextArea(440, 35, 180, 20, "Sem zadajte svoju pracovnu cinnost");
        root.getChildren().addAll(textAreaDate, textAreaHours, textAreaActivity);
    }

    /**
     * Vytvara konkretne textove pole na vstup od uzivatela.
     */
    private TextArea createTextArea(int width, int height, int layoutX, int layoutY, String text){
        TextArea textArea = new TextArea(text);
        textArea.setPrefSize(width, height);
        textArea.setLayoutX(layoutX);
        textArea.setLayoutY(layoutY);
        textArea.setWrapText(true);

        return textArea;
    }

    /**
     * Vytvora buttony riadiace aplikaciu
     */
    public Button creatButtons(String text, int x, int y, Pane root){
        ButtonCreate buttonCreate = new ButtonCreate(text, x, y, 100, 30);
        button = buttonCreate.getButton();

        root.getChildren().add(button);

        return button;
    }

    /**
     * Vytvara kompletny vypis (text + delete button)
     */
    public RecordBuilderForDB createTextAndButtonPanel(LocalDate date, double hours, int ID, VBox mainContainer){
        new TextAndButtonPanel(date, hours, textAreaActivity, mainContainer, ID);
        return new RecordBuilderForDB(date, hours, textAreaActivity.getText(), vacation, doctor, pn);
    }

    /**
     * Vytvara info-text pre uzivatela pri zadavani priezviska
     */
    public void createInfoText(Pane root, String text, int x, int y, int pref){
        TextFlow textFlow = new TextFlow(new Text(text));
        textFlow.setLayoutX(x);
        textFlow.setLayoutY(y);
        textFlow.setPrefWidth(pref);

        root.getChildren().add(textFlow);
    }

    /**
     * Vytvara textove pole pre vstup pri zadavani priezviska
     */
    public TextArea createTextAreaForPopupWindow(Pane root, int width, int height, int layoutX, int layoutY, String text){
        TextArea textArea = createTextArea(width, height, layoutX, layoutY, text);
        root.getChildren().add(textArea);
        return textArea;
    }
}
