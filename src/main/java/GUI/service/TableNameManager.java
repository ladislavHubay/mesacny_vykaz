package GUI.service;

import java.time.LocalDate;

public class TableNameManager {
    LocalDate date;
    String lastName;
    int month;
    int year;

    /**
     * Konstruktor na vytvaranie presneho nazvu databazy
     */
    public TableNameManager(LocalDate date, String lastName) {
        this.date = date;
        this.lastName = lastName;
        extractMonthAndYear();
    }

    /**
     * Ziskava mesiac a rok s datumu
     */
    private void extractMonthAndYear(){
        month = date.getMonthValue();
        year = date.getYear();
    }

    /**
     * Vytvory presny nazov mesacneho vykazu pre aktualny mesiac a rok
     */
    @Override
    public String toString() {
        return  lastName +
                "_vykaz_" +
                month +
                "_" +
                year;
    }
}
