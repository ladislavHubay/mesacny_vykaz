package GUI.eventHandlers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class RecordBuilderForDB {
    LocalDate date;
    double hours;
    String activity;
    boolean vacation;
    boolean doctor;
    boolean pn;

    /**
     * Vytvara konkretny riadok textu na zobrazenie z udajov zo vstupu od uzivatela.
     */
    public RecordBuilderForDB(LocalDate date, double hours, String activity, boolean vacation, boolean doctor, boolean pn) {
        this.date = date;
        this.hours = hours;
        this.activity = activity;
        this.vacation = vacation;
        this.doctor = doctor;
        this.pn = pn;
    }

    /**
     * Vrati naformatovany datum z konkretneho riadku.
     */
    public String getDate() {
        return date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    /**
     * Vrati cas (hodiny cinnosti) z konkretneho riadku.
     */
    public double getHours() {
        return hours;
    }

    /**
     * Vrati aktivitu z konkretneho riadku.
     */
    public String getActivity() {
        return activity;
    }

    /**
     * Vrati true / false - dovolenka z konkretneho riadku.
     */
    public boolean isVacation() {
        return vacation;
    }

    /**
     * Vrati true / false - navsteva lekara z konkretneho riadku.
     */
    public boolean isDoctor() {
        return doctor;
    }

    /**
     * Vrati true / false - PN z konkretneho riadku.
     */
    public boolean isPn() {
        return pn;
    }

    /**
     * Nastavuje true / false pre PN v konkretnom riadku.
     */
    public void setPn(boolean pn) {
        this.pn = pn;
    }

    /**
     * Nastavuje true / false pre navstevu lekara v konkretnom riadku.
     */
    public void setDoctor(boolean doctor) {
        this.doctor = doctor;
    }

    /**
     * Nastavuje true / false pre dovolenku v konkretnom riadku.
     */
    public void setVacation(boolean vacation) {
        this.vacation = vacation;
    }
}
