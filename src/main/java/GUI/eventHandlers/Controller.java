package GUI.eventHandlers;

import GUI.components.Components;
import GUI.components.WindowCreate;
import GUI.excel.ExcelGenerator;
import GUI.service.DatabaseService;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class Controller {
    private final Components components;
    private final DatabaseService dbService;
    private final String vacation = "Dovolenka";
    private final String holiday = "Sviatok";
    private final String doctor = "Vysetrenie u lekara";
    private final String sick = "PN";
    private String userName;
    private String salaryString;
    private String entitledVacationDaysString;
    private TextArea textAreaEntitledVacationDays;
    private TextArea textAreaSalary;
    private double entitledVacationDays;
    private double salary;
    private Stage mainStage;
    private final DateTimeFormatter formatter;
    private Button doctorButton;
    private Button vacationButton;
    private Button pnButton;
    private Button addButton;
    private Button deleteButton;
    private Button exitButton;
    private Button printButton;
    private Button updateButton;
    private String selectedFile;
    private List<String> allTableNames;
    private double xOffset = 0;
    private double yOffset = 0;

    /**
     * Konstruktor vytvara objekty DatabaseService, Components a spusti kontrolu ci uz bol uzivatel prihlaseny alebo nie
     */
    public Controller() {
        formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        dbService = new DatabaseService();
        components = new Components();
        tryReadUserDB();
    }

    /**
     * Metoda na zaklade existencie / neexistencie DB s uzivatelskym menom
     * spusta bud prve prihlasenie alebo pokracuje s existujucou DB.
     */
    private void tryReadUserDB(){
        userName = dbService.getUserData("lastName");

        if(userName == null || userName.isEmpty()){
            firstInputOfLastName();
        } else {
            mainStage = new Stage();
            Pane root = new Pane();
            VBox mainContainer = new VBox();
            DatabaseService.userName = userName;

            components.createTextAreas(root);
            handleButtonsAction(root, mainContainer);

            mainContainer.getChildren().add(root);

            Scene scene = new Scene(mainContainer, 640, 800);

            moveWindow(root);

            mainStage.setTitle("Mesačný výkaz");
            mainStage.setResizable(false);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm());
            mainStage.setScene(scene);
            mainStage.show();
        }
    }

    /**
     * Vytvara metody riadiace reakciu programu po stlaceni buttonov
     */
    private void handleButtonsAction(Pane root, VBox mainContainer) {
        addButtonAction(root, mainContainer);
        vacationButtonAction(root);
        doctorButtonAction(root);
        sickButtonAction(root);
        deleteDBButtonAction(root);
        exitButtonAction(root);
        printExcelButtonAction(root);
        updateButtonAction(root);
    }

    /**
     * Vytvara a riadi reakciu buttonu na pridanie jedneho riadka udajov zo vstupu do casti zobrazujuce suhrn.
     */
    private void addButtonAction(Pane root, VBox mainContainer) {
        addButton = components.creatButtons("pridaj", 520, 70, root);
        addButton.setOnAction(e -> {
            LocalDate date = dateValidate();
            if(date != null){
                String hours = numberValidate(components.textAreaHours.getText());
                if(hours != null){
                    RecordBuilderForDB recordBuilderForDB = components.createTextAndButtonPanel(
                            date, Double.parseDouble(hours), dbService.maxIDFromDB(date.format(formatter)) + 1, mainContainer);
                    Set<String> restrictedValues = new HashSet<>(Arrays.asList(vacation, doctor, sick));

                    if (!restrictedValues.contains(components.textAreaActivity.getText()) && recordBuilderForDB != null) {
                        recordBuilderForDB.setVacation(false);
                        recordBuilderForDB.setDoctor(false);
                        recordBuilderForDB.setPn(false);
                    }

                    if (recordBuilderForDB != null) {
                        if(!dbService.doesTableExist(String.valueOf(DatabaseService.getTableNameManager(date.format(formatter))))){
                            checkWeekAndHoliday(date);
                        }

                        dbService.insertData(
                                recordBuilderForDB.getDate(),
                                recordBuilderForDB.getHours(),
                                recordBuilderForDB.getActivity(),
                                recordBuilderForDB.isVacation(),
                                false,
                                recordBuilderForDB.isDoctor(),
                                recordBuilderForDB.isPn(),
                                false);
                    }
                }
            }
        });
    }

    /**
     * Metoda vytvori tabulku pre konkretny mesiac a vlozi vsetky sviatky a vikendy.
     */
    private void checkWeekAndHoliday(LocalDate date){
        dbService.createTable(date.format(formatter));
        HolidayAndVacationManager holidayAndVacationManager = new HolidayAndVacationManager(date);

        while (holidayAndVacationManager.dayCounter()) {
            String actualDate = holidayAndVacationManager.getActualDate().format(formatter);
            String holidayDate = holidayAndVacationManager.isHoliday();
            String weekend = holidayAndVacationManager.isWeek();

            if (holidayDate != null) {
                dbService.insertData(actualDate,0, holidayDate, false, true, false, false, false);
            } else if (weekend != null) {
                dbService.insertData(actualDate,0, weekend, false, false, false, false, true);
            }
        }
    }

    /**
     * Vytvara a riadi reakciu buttonu 'DOVOLENKA'
     */
    private void vacationButtonAction(Pane root) {
        vacationButton = components.creatButtons("dovolenka", 20, 70, root);
        vacationButton.setOnAction(e -> {
            components.textAreaActivity.setText(vacation);
            components.textAreaHours.setText("8");
            components.vacation = true;
            components.doctor = false;
            components.pn = false;
        });
    }

    /**
     * Vytvara a riadi reakciu buttonu 'LEKAR'
     */
    private void doctorButtonAction(Pane root) {
        doctorButton = components.creatButtons("lekar", 130, 70, root);
        doctorButton.setOnAction(e -> {
            components.textAreaActivity.setText(doctor);
            components.textAreaHours.setText("8");
            components.vacation = false;
            components.doctor = true;
            components.pn = false;
        });
    }

    /**
     * Vytvara a riadi reakciu buttonu 'PN'
     */
    private void sickButtonAction(Pane root) {
        pnButton = components.creatButtons("PN", 240, 70, root);
        pnButton.setOnAction(e -> {
            components.textAreaActivity.setText(sick);
            components.textAreaHours.setText("0");
            components.vacation = false;
            components.doctor = false;
            components.pn = true;
        });
    }

    /**
     * Vytvara a riadi reakciu buttonu 'exit'
     */
    private void exitButtonAction(Pane root) {
        exitButton = components.creatButtons("exit", 130, 110, root);
        exitButton.setOnAction(e -> {
            mainStage.close();
        });
    }

    /**
     * Vytvara a riadi reakciu buttonu 'delete DB'
     */
    private void deleteDBButtonAction(Pane root) {
        deleteButton = components.creatButtons("delete DB", 20, 110, root);
        deleteButton.setOnAction(e -> {
            showDeleteConfirmation("monthly_timesheet.db");
        });
    }

    /**
     * Vytvara a riadi reakciu buttonu 'print'
     */
    private void printExcelButtonAction(Pane root){
        printButton = components.creatButtons("print", 240, 110, root);
        printButton.setOnAction(e -> {
            choiceFileForPrint();
        });
    }

    private void updateButtonAction(Pane root){
        updateButton = components.creatButtons("update", 350, 110, root);
        updateButton.setOnAction(e -> {
            showUpdateWindow();
        });
    }

    /**
     * Vytvara vyskakovacie okno s informaciou o uspesnom / neuspeson vymazani databazi + zatvori aplikaciu.
     */
    public void deleteDatabaseAndResultInfo(String patch) {
        WindowCreate windowCreate = new WindowCreate(250, 100, "info");
        String text;

        if(new File(patch).delete()){
            text = "Databaza bola vymazana.";
        } else {
            text = "Databazu sa nepodarilo vymazat";
        }

        components.createInfoText(windowCreate.getRoot(), text, calculateTextCordinateX(text, 250), 10, 240);
        components.creatButtons("OK", 75, 45, windowCreate.getRoot()).setOnAction(e -> {
            windowCreate.getStage().close();
        });

        windowCreate.stageSetingsWithRoot();
    }

    /**
     * Vytvara vyskakovacie okno na upozornenie ci chce uzivatel skutocne vymazat databazu.
     */
    public void showDeleteConfirmation(String patch) {
        WindowCreate windowCreate = new WindowCreate(250, 100, "info");

        String text = "Skutocne chces vymazat databazu?";

        components.createInfoText(windowCreate.getRoot(), text, calculateTextCordinateX(text, 250), 10, 240);
        components.creatButtons("ano", 20, 45, windowCreate.getRoot()).setOnAction(e -> {
            windowCreate.getStage().close();
            mainStage.close();
            deleteDatabaseAndResultInfo(patch);
        });

        components.creatButtons("nie", 130, 45, windowCreate.getRoot()).setOnAction(e -> {
            windowCreate.getStage().close();
        });

        windowCreate.stageSetingsWithRoot();
    }

    /**
     * Vytvara vyskakovacie okno s moznostou zadat svoje priezvisko pri prvom spusteni aplikacie.
     * Priezvisko je potrebne kvoli vytvoreniu databazi pre uzivatela a pre spravne vygenerovanie nazvu excelu.
     * Po vlozeni priezviska sa pri dalsich spusteniach aplikacie uz neobrazi poziadavka na priezvisko.
     */
    public void firstInputOfLastName() {
        WindowCreate windowCreate = new WindowCreate(300, 270, "User name");

        components.createInfoText(windowCreate.getRoot(), "Zadaj svoje priezvisko:", 20, 5, 240);
        TextArea textAreaUserName = components.createTextAreaForPopupWindow(windowCreate.getRoot(), 260, 30, 20, 30, "");

        components.createInfoText(windowCreate.getRoot(), "Zadaj pocet dni dovolenky na rok:", 20, 75, 240);
        textAreaEntitledVacationDays = components.createTextAreaForPopupWindow(windowCreate.getRoot(), 260, 30, 20, 100, "");

        components.createInfoText(windowCreate.getRoot(), "Zadaj svoj mesacny plat:", 20, 145, 240);
        textAreaSalary = components.createTextAreaForPopupWindow(windowCreate.getRoot(), 260, 30, 20, 170, "");

        components.creatButtons("Potvrdit", 165, 220, windowCreate.getRoot()).setOnAction(e -> {
            userName = textAreaUserName.getText();
            entitledVacationDaysString = textAreaEntitledVacationDays.getText();
            salaryString = textAreaSalary.getText();

            if(userName != null && !userName.isEmpty()
            && entitledVacationDaysString != null && !entitledVacationDaysString.isEmpty()
            && salaryString != null && !salaryString.isEmpty()){

                entitledVacationDays = holidayAndSalaryValidate(entitledVacationDaysString);
                salary = holidayAndSalaryValidate(salaryString);

                if(entitledVacationDays != 0 && salary != 0){
                    dbService.createTableForLastName();
                    dbService.insertDataToUserTable(userName, String.valueOf(entitledVacationDays), String.valueOf(salary));

                    windowCreate.getStage().close();
                    tryReadUserDB();
                }
            }
        });

        components.creatButtons("exit", 35, 220, windowCreate.getRoot()).setOnAction(e -> {
            windowCreate.getStage().close();
        });

        windowCreate.stageSetingsWithRoot();
    }
    
    /**
     * Vytvara vyskakovacie okno s informacym textom. Meni sa len informacny textpodla potreby.
     */
    private void showAlertMessage(String text) {
        buttonsEnabled(true);
        WindowCreate windowCreate = new WindowCreate(250, 100, "info");

        components.createInfoText(windowCreate.getRoot(), text, calculateTextCordinateX(text, 250), 10, 240);
        components.creatButtons("OK", 75, 45, windowCreate.getRoot()).setOnAction(e -> {
            buttonsEnabled(false);
            windowCreate.getStage().close();
        });

        windowCreate.stageSetingsWithRoot();
    }

    /**
     * Metoda vytvara okno s moznostou zmenit pocet dni dovolenky a vysku platu.
     */
    private void showUpdateWindow() {
        buttonsEnabled(true);
        WindowCreate windowCreate = new WindowCreate(250, 220, "update");

        components.createInfoText(windowCreate.getRoot(), "Aktualny pocet dni dovolenky: " +
                dbService.getUserData("vacation"), 20, 10, 240);
        components.createInfoText(windowCreate.getRoot(), "Novy pocet dni dovolenky:", 20, 50, 240);
        textAreaEntitledVacationDays = components.createTextAreaForPopupWindow(
                windowCreate.getRoot(), 60, 20, 170, 40, "");

        components.createInfoText(windowCreate.getRoot(), "Aktualna vyska platu: " +
                dbService.getUserData("salary") + " EUR", 20, 90, 240);
        components.createInfoText(windowCreate.getRoot(), "Nova vyska platu:", 20, 130, 240);
        textAreaSalary = components.createTextAreaForPopupWindow(
                windowCreate.getRoot(), 60, 20, 170, 120, "");

        components.creatButtons("OK", 130, 170, windowCreate.getRoot()).setOnAction(e -> {
            updateVacationAndSalary();
            buttonsEnabled(false);
            windowCreate.getStage().close();
        });

        components.creatButtons("back", 20, 170, windowCreate.getRoot()).setOnAction(e -> {
            buttonsEnabled(false);
            windowCreate.getStage().close();
        });

        windowCreate.stageSetingsWithRoot();
    }

    /**
     * Metoda zmeni hodnoty poctu dni dovolenky a platu v databaze.
     */
    private void updateVacationAndSalary() {
        if(holidayAndSalaryValidate(textAreaEntitledVacationDays.getText()) != 0){
            dbService.updateDataToUserTable("vacation", textAreaEntitledVacationDays.getText());
        }

        if(holidayAndSalaryValidate(textAreaSalary.getText()) != 0){
            dbService.updateDataToUserTable("salary", textAreaSalary.getText());
        }
    }

    /**
     * Vytvara vyskakovacie okno s moznostami vyberu konkretnej tabulky na vyexportovanie do excelu.
     */
    private void choiceFileForPrint() {
        buttonsEnabled(true);
        WindowCreate windowCreate = new WindowCreate(250, 100, "print");

        if(isListNotEmpty(windowCreate.getRoot())){
            return;
        }

        components.createInfoText(windowCreate.getRoot(), "vykaz", 20, 15, 40);
        components.creatButtons("print", 20, 45, windowCreate.getRoot()).setOnAction(e -> {

            List<Map<String, Object>> file = dbService.getAllRows(selectedFile);

            HolidayAndAbsenceCalculator calculator = new HolidayAndAbsenceCalculator(file);

            for (String tableName : allTableNames) {
                calculator.checkTheDate(dbService.getAllRows(tableName));
            }

            HolidayAndVacationManager holidayAndVacationManager = new HolidayAndVacationManager(LocalDate.parse(file.get(0).get("date").toString(), formatter));

            double usedVacationDays = calculator.getVacationDays() / 8;

            ExcelGenerator excelGenerator = new ExcelGenerator(selectedFile, file);
            excelGenerator.createExcel();
            excelGenerator.writeSumRow(calculator.getHours(), Double.parseDouble(dbService.getUserData("salary")));
            excelGenerator.writeSumOfWorkDaysAndVacationDays(
                    holidayAndVacationManager.workingDays(), usedVacationDays,
                    Double.parseDouble(dbService.getUserData("vacation")) - usedVacationDays);

            buttonsEnabled(false);
            windowCreate.getStage().close();
        });

        components.creatButtons("back", 130, 45, windowCreate.getRoot()).setOnAction(e -> {
            buttonsEnabled(false);
            windowCreate.getStage().close();
        });

        windowCreate.stageSetingsWithRoot();
    }

    /**
     * Nastavuje buttony na aktivne / neaktivne.
     */
    private void buttonsEnabled(boolean enabled){
        vacationButton.setDisable(enabled);
        doctorButton.setDisable(enabled);
        pnButton.setDisable(enabled);
        addButton.setDisable(enabled);
        deleteButton.setDisable(enabled);
        exitButton.setDisable(enabled);
        printButton.setDisable(enabled);
        updateButton.setDisable(enabled);
    }

    /**
     * Na zaklade dlzky textu a dlzky priesoru na text vypocita bod x kde sa ma zacinat text v okne
     */
    private int calculateTextCordinateX(String text, int width){
        return (int) (width - (new Text(text)).getLayoutBounds().getWidth()) / 2;
    }

    /**
     * Metoda na validaciu spravneho vstupu pre datum +
     * vyvolanie vyskakovacieho okna na upozornenie nespravneho formatu datumu.
     */
    private LocalDate dateValidate(){
        LocalDate date;

        try {
            date = LocalDate.parse(components.textAreaDate.getText(), formatter);
        } catch (DateTimeParseException e) {
            showAlertMessage("Datum nie je v pozadovanom formate");
            return null;
        }
        return date;
    }

    /**
     * Metoda na validaciu spravneho vstupu pre hodiny +
     * vyvolanie vyskakovacieho okna na upozornenie nespravneho formatu vstupu pre hodiny (odpracovany cas).
     */
    private String numberValidate(String value){
        if(value == null || value.isEmpty()){
            showAlertMessage("Cas nie je v pozadovanom formate");
            return null;
        }

        try {
            Double.parseDouble(value);
        } catch (NumberFormatException e) {
            showAlertMessage("Cas nie je v pozadovanom formate");
            return null;
        }
        return value;
    }

    /**
     * Metoda kontroluje ci je vstupny pocet dovolenkovych dni alebo platu realen cislo rozne od 0.
     */
    private double holidayAndSalaryValidate(String value){
        if(value == null || value.isEmpty()){
            return 0;
        }

        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }

    /**
     * Vytvara vyber moznosti zo zoznamu existujucich tabuliek.
     */
    private void choice(Pane root, List<String> list) {
        ComboBox<String> excelName = new ComboBox<>();

        for (String name : list) {
            excelName.getItems().add(name);
        }

        excelName.setValue(list.get(0));
        excelName.setLayoutX(60);
        excelName.setLayoutY(10);
        selectedFile = list.get(0);

        excelName.setOnAction(e -> {
            selectedFile = excelName.getValue();
        });

        root.getChildren().add(excelName);
    }

    /**
     * Preveruje ci existuju data (tabulky) na vytlacenie.
     */
    private boolean isListNotEmpty(Pane root){
        allTableNames = dbService.listAllTables();
        if(allTableNames.isEmpty()){
            showAlertMessage("Nenasiel sa ziadny vykaz na zobrazenie");
            return true;
        }
        choice(root, allTableNames);
        return false;
    }

    /**
     * Metoda vypne ram grafickeho okna vratane moznosti minimalizovat, maximalizovat a zrusit.
     * Zaroven umoznuje posun grafickeho okno pomocou mysi.
     */
    private void moveWindow(Pane root){
        mainStage.initStyle(StageStyle.UNDECORATED);
        root.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        root.setOnMouseDragged(event -> {
            mainStage.setX(event.getScreenX() - xOffset);
            mainStage.setY(event.getScreenY() - yOffset);
        });
    }
}
