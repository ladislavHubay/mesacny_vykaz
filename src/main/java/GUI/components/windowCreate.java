package GUI.components;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class windowCreate {
    Stage stage;
    Pane root;
    Scene scene;
    int stageLayoutX;
    int stageLayoutY;
    String stageTitle;

    /**
     * Konstruktor vytvara vyskakovacie okno s informaciou pre uzivatela.
     */
    public windowCreate(int stageLayoutX, int stageLayoutY, String stageTitle) {
        stage = new Stage();
        root = new Pane();
        this.stageLayoutX = stageLayoutX;
        this.stageLayoutY = stageLayoutY;
        this.stageTitle = stageTitle;
    }

    /**
     * Vytvara 'stage' s konkretnymi nastaveniami.
     */
    public void stageSetingsWithRoot() {
        scene = new Scene(root, stageLayoutX, stageLayoutY);
        stage.setTitle(stageTitle);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Vracia 'rook' - objekt triedy Pane z konkretneho vyskakovacieho okna.
     */
    public Pane getRoot() {
        return root;
    }

    /**
     * Vracia 'stage' - objekt triedy Stage z konkretneho vyskakovacieho okna.
     */
    public Stage getStage() {
        return stage;
    }
}
