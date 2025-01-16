package GUI.eventHandlers;

import de.jollyday.Holiday;
import de.jollyday.HolidayManager;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Set;

public class HolidayAndVacationManager {
    YearMonth month;
    LocalDate endedDay;
    LocalDate actualDate;
    HolidayManager manager;
    Set<Holiday> holidays;

    /**
     * Konstruktor na zaklade datumu vytvory konkretny mesiac na vyhladanie sviatkov a vikendov.
     */
    public HolidayAndVacationManager(LocalDate date) {
        month = YearMonth.from(date);
        actualDate = month.atDay(1);
        endedDay = month.atEndOfMonth();
        manager = HolidayManager.getInstance();
        holidays = manager.getHolidays(date.getYear(), "SK");
    }

    /**
     * Metoda kontroluje ci aktualny den v mesiaci je mensi ako posledny den v mesiaci. Vrati true ak ano, false ak nie.
     */
    public boolean dayCounter() {
        return !actualDate.isAfter(endedDay);
    }

    /**
     * Metoda kontroluje ci je aktualny datum sviatok. Ak ano vrati datum. Ak nie vrati null.
     */
    public LocalDate isHoliday(){
        for (Holiday holiday : holidays) {
            if (holiday.getDate().equals(actualDate)) {
                return actualDate;
            }
        }

        return null;
    }

    /**
     * Metoda kontroluje ci je aktualny den vikend. Ak ano vrati aktualny den (sobota / nedela). Ak nie vrati null.
     */
    public String isWeek() {
        DayOfWeek day = actualDate.getDayOfWeek();
        String dayOfWeek = day.getDisplayName(TextStyle.FULL, new Locale("sk"));
        String returnedDate;

        if (dayOfWeek.equalsIgnoreCase("sobota")) {
            returnedDate = "sobota";
        } else if (dayOfWeek.equalsIgnoreCase("nedeľa")) {
            returnedDate = "nedela";
        } else {
            returnedDate = null;
        }
        actualDate = actualDate.plusDays(1);

        return returnedDate;
    }

    /**
     * Metoda spocita pracovne dni v mesiaci.
     */
    public int workingDays(){
        int workDays = 0;
        for (int day = 1; day <= month.lengthOfMonth(); day++) {
            if(isHoliday() == null && isWeek() == null){
                workDays++;
            }
            actualDate = actualDate.plusDays(1);
        }

        return workDays;
    }

    /**
     * Vrati aktualny datum.
     */
    public LocalDate getActualDate() {
        return actualDate;
    }
}
