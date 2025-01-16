package GUI.components;

import javafx.scene.control.Button;

public class ButtonCreate {
    String text;
    int layoutX;
    int layoutY;
    int prefSizeX;
    int prefSizeY;
    boolean disable;
    Button button;

    /**
     * Konstruktor vytvara button s konkretnymi parametrami.
     */
    public ButtonCreate(String text, int layoutX, int layoutY, int prefSizeX, int prefSizeY) {
        this.text = text;
        this.layoutX = layoutX;
        this.layoutY = layoutY;
        this.prefSizeX = prefSizeX;
        this.prefSizeY = prefSizeY;
        this.disable = false;
        buttonSettings();
    }

    /**
     * Nastavuje konkretne parametre pre button.
     */
    private void buttonSettings(){
        button = new Button(text);
        button.setLayoutX(layoutX);
        button.setLayoutY(layoutY);
        button.setPrefSize(prefSizeX, prefSizeY);
        button.setDisable(disable);
    }

    /**
     * Vracia vytvoreny button.
     */
    public Button getButton() {
        return button;
    }
}
