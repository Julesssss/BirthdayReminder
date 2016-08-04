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
}
