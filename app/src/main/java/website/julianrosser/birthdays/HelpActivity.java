package website.julianrosser.birthdays;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class HelpActivity extends AppCompatActivity {

    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Receive help, submit feedback or suggest a new feature", Snackbar.LENGTH_LONG)
                        .setAction("Send Email", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                emailMe();
                            }
                        }).show();

                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("Email FAB")
                        .build());

            }
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Obtain the shared Tracker instance.
        mTracker = getDefaultTracker();
    }

    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(R.xml.global_tracker);
        }
        return mTracker;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTracker.setScreenName("Help");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public void emailMe() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + "julianrosser91@gmail.com"));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Birthday Reminder");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Message.");

        startActivity(Intent.createChooser(emailIntent, "Choose your preferred email app"));

        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Action")
                .setAction("Send email button")
                .build());
    }

}
