package GUI.eventHandlers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class HolidayAndAbsenceCalculator {
    double vacationDays = 0;
    double doctorDays = 0;
    double sickDays = 0;
    double hours = 0;
    LocalDate selectedDate;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    /**
     * Konstruktor nastavy datum do pozadovaneho formatu a spusti vypocet odpracovanych hodin za dany mesiac.
     */
    public HolidayAndAbsenceCalculator(List<Map<String, Object>> file) {
        this.selectedDate = LocalDate.parse(file.get(0).get("date").toString(), formatter);
        calculateHours(file);
    }

    /**
     * Metoda spocita pocet hodin dovolenky, sviatku, lekarskeho vysetrenia a PN.
     */
    private void calculateTheOutOfWorkTime(List<Map<String, Object>> rows){
        for (Map<String, Object> row : rows){
            if((int)row.get("vacation") == 1){
                vacationDays = vacationDays + (double) row.get("hours");
            }
            if((int)row.get("doctor") == 1){
                doctorDays = doctorDays + (double) row.get("hours");
            }
            if((int)row.get("pn") == 1){
                sickDays = sickDays + (double) row.get("hours");
            }
        }
    }

    /**
     * Metoda zabespeci aby sa mimopracovne cinnosti (dovolenka, sviatok, lekar, PN)
     * spocitali iba v dany rok a iba po dany mesiac.
     */
    public void checkTheDate(List<Map<String, Object>> rows){
        LocalDate date = LocalDate.parse(rows.get(0).get("date").toString(), formatter);

        if(!selectedDate.isBefore(date) && selectedDate.getYear() == date.getYear()){
            calculateTheOutOfWorkTime(rows);
        }
    }

    /**
     * Metoda spocita odpracovane hodiny v dany mesiac.
     */
    private void calculateHours(List<Map<String, Object>> file){
        for (Map<String, Object> row : file){
            if((int)row.get("vacation") == 0 &&
            (int)row.get("doctor") == 0 &&
            (int)row.get("pn") == 0){
                hours = hours + (double) row.get("hours");
            }
        }
    }

    /**
     * Metoda vrati pocet hodin dovolenky.
     */
    public double getVacationDays() {
        return vacationDays;
    }

    /**
     * Metoda vrati pocet hodin u lekara.
     */
    public double getDoctorDays() {
        return doctorDays;
    }

    /**
     * Metoda vrati pocet hodin na PN.
     */
    public double getSickDays() {
        return sickDays;
    }

    /**
     * Metoda vrati pocet celkovych odpracovanych hodin za dany mesiac.
     */
    public double getHours() {
        return hours;
    }
}
