package GUI.excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ExcelGenerator {
    private final String sheetName;
    private Workbook workbook;
    private Sheet sheet;
    private Row row;
    private final String[] COLUMNS_NAME = {"datum", "hod.", "aktivita"};
    private final List<Map<String, Object>> rows;
    private static final String[] columns = {"date", "hours", "activity"};
    private ExcelCellManager excelCellManager;

    /**
     * Konstruktor vytvara excel s datami.
     */
    public ExcelGenerator(String sheetName, List<Map<String, Object>> file) {
        this.sheetName = sheetName;
        this.rows = file;
    }

    /**
     * Metoda vytvori dokument excel so zakladnymi nastaveniami.
     */
    public void createExcel(){
        workbook = new XSSFWorkbook();
        sheet = workbook.createSheet(sheetName);

        sheet.setColumnWidth(0, 12 * 256);
        sheet.setColumnWidth(1, 8 * 256);
        sheet.setColumnWidth(2, 14 * 256);

        writeHeader();
        writeData();
    }

    /**
     * Metoda vytvori a do excelu zapise hlavicku pozadovanej tabulky.
     */
    private void writeHeader(){
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 2, 6));
        row = sheet.createRow(0);

        for (int i = 0; i < COLUMNS_NAME.length; i++) {
            excelCellManager = new ExcelCellManager(workbook);
            excelCellManager.createFontForCell(true);
            excelCellManager.createCenterForCell(true);

            excelCellManager.createBorderTop(false);
            if(i == 0){
                excelCellManager.createBorderLeft(false);
            } else if (i == 1) {
                excelCellManager.createBorderRight(true);
                excelCellManager.createBorderLeft(true);
            } else {
                excelCellManager.createBorderLeft(true);
                excelCellManager.createOneCell(row, 6);
                excelCellManager.createBorderRight(false);
                excelCellManager.addStyleIntoTheCell();
            }
            excelCellManager.createBorderBottom(true);

            excelCellManager.createOneCell(row, i);
            excelCellManager.addTextIntoTheCell(COLUMNS_NAME[i]);
            excelCellManager.addStyleIntoTheCell();
        }
    }

    /**
     * Metoda vytvori a do excelu zapise data o praci ako je datum, pocet hodin a aktivita.
     */
    private void writeData(){
        for (int i = 1; i <= rows.size(); i++){
            sheet.addMergedRegion(new CellRangeAddress(i, i, 2, 6));
            row = sheet.createRow(i);

            String freeDay = rows.get(i - 1).get(columns[2]).toString();

            for (int j = 0; j < COLUMNS_NAME.length; j++){
                excelCellManager = new ExcelCellManager(workbook);

                if(j == 0){
                    excelCellManager.createBorderLeft(false);
                    excelCellManager.createCenterForCell(true);
                } else if (j == 1) {
                    excelCellManager.createBorderLeft(true);
                    excelCellManager.createBorderRight(true);
                    excelCellManager.createCenterForCell(true);
                } else {
                    excelCellManager.createWrapText(true);
                    excelCellManager.createCenterForCell(false);
                    excelCellManager.createOneCell(row, 6);
                    excelCellManager.createBorderRight(false);
                    excelCellManager.addStyleIntoTheCell();
                }

                // Upravit kontrolu (nesmie byt podla nazvu aktivity -sobota, nedela, sviatok).
                // V pripade ak sa pracuje cez volny den tak vznika problem vyfarbenia buniek.

                if(freeDay.equalsIgnoreCase("sobota") ||
                        freeDay.equalsIgnoreCase("nedela") ||
                        freeDay.equalsIgnoreCase("sviatok")){
                    excelCellManager.createColorForCell(112, 173, 71);
                }

                createCellAndWriteTextAndAddStyle(j, rows.get(i - 1).get(columns[j]).toString());

                // vyriesit vysku bunky v pripade viacriadkoveho vypisu (zalomenie textu)

                //if(j == 2){
                //    excelCellManager.vypocitajVysku(sheet, 2, row);
                //}

                if(i == rows.size()){
                    excelCellManager.createBorderBottom(false);
                }
            }
        }
    }

    /**
     * Vytvory spodnu cast tabulky obsahujucu pocet odpracovanych hodin za mesiac a vyplatenu sumu.
     */
    public void writeSumRow(double hours, double value){
        row = sheet.createRow(rows.size() + 1);

        excelCellManager = new ExcelCellManager(workbook);
        excelCellManager.createCenterForCell(true);
        excelCellManager.createFontForCell(true);
        excelCellManager.createBorderLeft(false);
        excelCellManager.createBorderRight(true);
        excelCellManager.createBorderBottom(false);
        createCellAndWriteTextAndAddStyle(0, "hod. Spolu:");

        excelCellManager = new ExcelCellManager(workbook);
        excelCellManager.createCenterForCell(true);
        excelCellManager.createBorderLeft(true);
        excelCellManager.createBorderRight(false);
        excelCellManager.createBorderBottom(false);
        createCellAndWriteTextAndAddStyle(1, String.valueOf(hours));

        row = sheet.createRow(rows.size() + 2);

        excelCellManager = new ExcelCellManager(workbook);
        excelCellManager.createCenterForCell(true);
        excelCellManager.createFontForCell(true);
        createCellAndWriteTextAndAddStyle(0, "suma:");

        excelCellManager = new ExcelCellManager(workbook);
        excelCellManager.createCenterForCell(true);
        createCellAndWriteTextAndAddStyle(1, String.valueOf(value));
    }

    /**
     * Metoda vytvara a vypisuje suhrnny prehlad o pocte pracovnych dni,
     * pocte cerpanych dni dovolenky a pocte zostavajucej dovolenky.
     */
    public void writeSumOfWorkDaysAndVacationDays(int workingDays, double vacationDays, double remainingVacationDays){
        sheet.addMergedRegion(new CellRangeAddress(rows.size() + 4, rows.size() + 4, 0, 2));
        row = sheet.createRow(rows.size() + 4);
        excelCellManager = new ExcelCellManager(workbook);
        createCellAndWriteTextAndAddStyle(0, "Pocet pracovnych dni:");
        createCellAndWriteTextAndAddStyle(3, String.valueOf(workingDays));

        sheet.addMergedRegion(new CellRangeAddress(rows.size() + 5, rows.size() + 5, 0, 2));
        row = sheet.createRow(rows.size() + 5);
        excelCellManager = new ExcelCellManager(workbook);
        createCellAndWriteTextAndAddStyle(0, "Pocet cerpanych dni dovolenky:");
        createCellAndWriteTextAndAddStyle(3, String.valueOf(vacationDays));

        sheet.addMergedRegion(new CellRangeAddress(rows.size() + 6, rows.size() + 6, 0, 2));
        row = sheet.createRow(rows.size() + 6);
        createCellAndWriteTextAndAddStyle(0, "Pocet zostavajucich dni dovolenky:");
        createCellAndWriteTextAndAddStyle(3, String.valueOf(remainingVacationDays));

        saveFile();
    }

    /**
     * Metoda vytvora bunku s pozadovanymi parametrami zhrnutymi za cely mesiac.
     */
    private void createCellAndWriteTextAndAddStyle(int columnIndex, String text){
        excelCellManager.createOneCell(row, columnIndex);
        excelCellManager.addTextIntoTheCell(String.valueOf(text));
        excelCellManager.addStyleIntoTheCell();
    }

    /**
     * Metoda vytvori nazov suboru a uklada subor.
     */
    private void saveFile(){
        try (FileOutputStream fileOut = new FileOutputStream(sheetName + ".xlsx")) {
            workbook.write(fileOut);
        } catch (IOException e) {
            System.err.println("Chyba pri ukladaní súboru: " + e.getMessage());
        }

        try {
            workbook.close();
        } catch (IOException e) {
            System.err.println("Chyba pri zatváraní workbooku: " + e.getMessage());
        }
    }
}
