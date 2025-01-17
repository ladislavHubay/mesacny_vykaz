package GUI.excel;

import javafx.scene.text.Text;
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

    /**
     * Metoda vypocita a nastavi potrebnu vysku bunky v pripade viacriadkoveho textu v bunke po zalomeni.
     */
    public void calculateTheRowHeight(Sheet sheet, int startColumn, int endColumn, Row row){
        String cellValue = cell.getStringCellValue();
        int lineCount = (int) Math.ceil((double) cellValue.length() / calculateTheTextWidth(sheet, startColumn, endColumn));
        row.setHeight((short) (lineCount * (new Text(cellValue).getLayoutBounds().getHeight() * 0.75 * 20)));
    }

    /**
     * Metoda vypocita maximalny pocet znakov ktore sa zmestia v jednom riadku do bunky.
     */
    public int calculateTheTextWidth(Sheet sheet, int startColumn, int endColumn){
        int totalWidth = 0;

        for (int col = startColumn; col <= endColumn; col++) {
            totalWidth += (int) (sheet.getColumnWidth(col) / 256.0);
        }

        double charWidth = 0.7;
        return (int) (totalWidth / charWidth);
    }

    /**
     * Metoda vrati bunku.
     */
    public Cell getCell() {
        return cell;
    }
}
