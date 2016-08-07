package website.julianrosser.birthdays;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

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

    public static int getHighlightColor(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        if (prefs.getString(context.getResources().getString(R.string.pref_theme_key), "0").equals("0")) {
            return R.color.material_lime_500;
        } else if (prefs.getString(context.getResources().getString(R.string.pref_theme_key), "0").equals("1")) {
            return R.color.blue_accent_400;
        } else if (prefs.getString(context.getResources().getString(R.string.pref_theme_key), "0").equals("2")) {
            return R.color.blue_accent_700;
        } else {
            return R.color.blue_accent_400;
        }
    }
}
