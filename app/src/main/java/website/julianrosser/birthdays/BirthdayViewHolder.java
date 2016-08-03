package website.julianrosser.birthdays;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * ViewHolder class to hold view references to be used in recyclerview.
 */
public class BirthdayViewHolder extends RecyclerView.ViewHolder {


    // TextView references
    TextView textDateDay;
    TextView textDateMonth;
    TextView textName;
    TextView textAge;
    TextView textDaysRemaining;
    ImageView imageAlarm;
    View container;
    Typeface typeLight;

    public BirthdayViewHolder(View itemView) {
        super(itemView);

        // Set up references
        container = itemView.findViewById(R.id.list_container);
        textName = (TextView) itemView.findViewById(R.id.name);
        textDaysRemaining = (TextView) itemView.findViewById(R.id.days_remaining);
        textAge = (TextView) itemView.findViewById(R.id.textViewAge);
        textDateDay = (TextView) itemView.findViewById(R.id.dateDay);
        textDateMonth = (TextView) itemView.findViewById(R.id.dateMonth);
        imageAlarm = (ImageView) itemView.findViewById(R.id.alarmImage);
        typeLight = Typeface.createFromAsset(MainActivity.getAppContext().getResources().getAssets(), "Roboto-Light.ttf");
    }
}