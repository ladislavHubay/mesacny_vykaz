package GUI.components;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Objects;

public class WindowCreate {
    Stage stage;
    Pane root;
    Scene scene;
    int stageLayoutX;
    int stageLayoutY;
    String stageTitle;
    private double xOffset = 0;
    private double yOffset = 0;

    /**
     * Konstruktor vytvara vyskakovacie okno s informaciou pre uzivatela.
     */
    public WindowCreate(int stageLayoutX, int stageLayoutY, String stageTitle) {
        stage = new Stage();
        stage.initStyle(StageStyle.UNDECORATED);
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
        root.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        root.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });

        stage.setTitle(stageTitle);
        stage.setResizable(false);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm());
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
