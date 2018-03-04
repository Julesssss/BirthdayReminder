package website.julianrosser.birthdays;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.TypedValue;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    public static Date stringToDate(String birthdayString) {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            if (birthdayString.startsWith("-")) {
                birthdayString = birthdayString.replaceFirst("-", "1990");
            }
            date = format.parse(birthdayString);
            date.setYear(date.getYear() + 1900);
            System.out.println(date);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static boolean hasYearOfBirth(String birthdayString) {
        return ! birthdayString.startsWith("-");
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

    public static boolean isStringEmpty(String s) {
        return s == null || s.equals("");
    }

    // Helper method for getting exact pixel size for device from density independent pixels
    public static int getPixelsFromDP(Context context, int px) {
        Resources r = context.getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, r.getDisplayMetrics());
    }
}
