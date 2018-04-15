package website.julianrosser.birthdays.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import com.google.android.gms.analytics.GoogleAnalytics
import com.google.android.gms.analytics.HitBuilders
import com.google.android.gms.analytics.Tracker
import kotlinx.android.synthetic.main.activity_help.*
import website.julianrosser.birthdays.BuildConfig
import website.julianrosser.birthdays.R

class HelpActivity : BaseActivity() {

    private var mTracker: Tracker? = null

    private val defaultTracker: Tracker?
        @Synchronized get() {
            if (mTracker == null) {
                val analytics = GoogleAnalytics.getInstance(this)
                mTracker = analytics.newTracker(R.xml.global_tracker)
            }
            return mTracker
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)

        // Set up toolbar reference
        setSupportActionBar(toolbar)

        // Set up FloatingActionButton ref and listener
        fab.setOnClickListener { view ->
            Snackbar.make(view, R.string.email_me_text, Snackbar.LENGTH_LONG)
                    .setAction(R.string.send_email) { emailMe() }.show()

            mTracker!!.send(HitBuilders.EventBuilder()
                    .setCategory("Action")
                    .setAction("Email FAB")
                    .build())
        }

        // Show home button on toolbar
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }

        // Obtain the shared Tracker instance.
        mTracker = defaultTracker
    }

    override fun onResume() {
        super.onResume()
        mTracker!!.setScreenName("Help")
        mTracker!!.send(HitBuilders.ScreenViewBuilder().build())
    }

    // Set Activity theme depending on user preference
    private fun setTheme() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        setTheme(
                when (prefs.getString(resources.getString(R.string.pref_theme_key), "0")) {
                    "0" -> R.style.BlueTheme
                    "1" -> R.style.PinkTheme
                    "2" -> R.style.GreenTheme
                    else -> R.style.BlueTheme
                }
        )
    }

    private fun emailMe() {
        val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + "julianross" + "er91@gma" + "il.com"))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Birthday Reminder")

        emailIntent.putExtra(Intent.EXTRA_TEXT, "App Version: " + BuildConfig.VERSION_NAME + "\n\n")

        startActivity(Intent.createChooser(emailIntent, getString(R.string.preffered_email)))

        mTracker!!.send(HitBuilders.EventBuilder()
                .setCategory("Action")
                .setAction("Send email button")
                .build())
    }

}
