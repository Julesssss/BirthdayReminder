package website.julianrosser.birthdays;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    public static Date stringToDate(String birthdayString) {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = format.parse(birthdayString);
            System.out.println(date);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String getDateSuffix(int date) {
        if (date == 11 || date == 12 || date == 13) {
            return "th";
        } else if (date % 10 == 1) {
            return "st";
        } else if (date % 10 == 2) {
            return "nd";
        } else if (date % 10 == 3) {
            return "rd";
        } else {
            return "th";
        }
    }
}
