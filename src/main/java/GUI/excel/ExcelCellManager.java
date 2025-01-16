package GUI.excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFColor;

import java.awt.Color;

public class ExcelCellManager {
    CellStyle style;
    Workbook workbook;
    Cell cell;

    /**
     * Konstruktor vytvara presne naformatovanu bunku v excel.
     */
    public ExcelCellManager(Workbook workbook) {
        style = workbook.createCellStyle();
        this.workbook = workbook;
    }

    /**
     * Metoda vytvara font pisma (bold) v bunke.
     */
    public void createFontForCell(boolean bold){
        Font font = workbook.createFont();
        font.setBold(bold);
        style.setFont(font);
    }

    /**
     * Metoda centruje text do stredu v bunke.
     */
    public void createCenterForCell(boolean center){
        if(center){
            style.setAlignment(HorizontalAlignment.CENTER);
        }
    }

    /**
     * Metoda vytvara oramovanie bunky - vrch. V pripade top = true ciara bude tenka, false stredna hrubka.
     */
    public void createBorderTop(boolean top){
        if (top) {
            style.setBorderTop(BorderStyle.THIN);
        } else {
            style.setBorderTop(BorderStyle.MEDIUM);
        }
    }

    /**
     * Metoda vytvara oramovanie bunky - spodok. V pripade bottom = true ciara bude tenka, false stredna hrubka.
     */
    public void createBorderBottom(boolean bottom){
        if (bottom) {
            style.setBorderBottom(BorderStyle.THIN);
        } else {
            style.setBorderBottom(BorderStyle.MEDIUM);
        }
    }

    /**
     * Metoda vytvara oramovanie bunky - lava strana. V pripade top = true ciara bude tenka, false stredna hrubka.
     */
    public void createBorderLeft(boolean left){
        if (left) {
            style.setBorderLeft(BorderStyle.THIN);
        } else {
            style.setBorderLeft(BorderStyle.MEDIUM);
        }
    }

    /**
     * Metoda vytvara oramovanie bunky - prava strana. V pripade top = true ciara bude tenka, false stredna hrubka.
     */
    public void createBorderRight(boolean right){
        if (right) {
            style.setBorderRight(BorderStyle.THIN);
        } else {
            style.setBorderRight(BorderStyle.MEDIUM);
        }
    }

    /**
     * Metoda zabespecuje zalomenie textu v bunke.
     */
    public void createWrapText(boolean wrapText){
        style.setWrapText(wrapText);
    }

    /**
     * Metoda umiestni bunku v riadku row a stlpci columnIndex.
     */
    public void createOneCell(Row row, int columnIndex){
        cell = row.createCell(columnIndex);
    }

    /**
     * Metoda nastavi farbu pozadia pre bunku.
     */
    public void createColorForCell(int red, int green, int blue){
        XSSFColor color = new XSSFColor(new Color(red, green, blue), null);
        style.setFillForegroundColor(color);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    }

    /**
     * Metoda prida text do bunky.
     */
    public void addTextIntoTheCell(String text){
        cell.setCellValue(text);
    }

    /**
     * Metoda pripne styl (formatovanie) danej bunky.
     */
    public void addStyleIntoTheCell(){
        cell.setCellStyle(style);
    }

    public void vypocitajVysku(Sheet sheet, int columnIndex, Row row){
        String cellValue = cell.getStringCellValue();

        int columnWidth = sheet.getColumnWidth(columnIndex) / 256;
        double charWidth = 1.2;
        int charsPerLine = (int) (columnWidth / charWidth);
        int lineCount = (int) Math.ceil((double) cellValue.length() / charsPerLine);

        row.setHeight((short) (lineCount * 55));
    }
}
