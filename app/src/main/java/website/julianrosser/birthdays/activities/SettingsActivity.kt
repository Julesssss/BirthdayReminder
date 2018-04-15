package website.julianrosser.birthdays.activities

import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import com.google.android.gms.analytics.GoogleAnalytics
import com.google.android.gms.analytics.HitBuilders
import com.google.android.gms.analytics.Tracker
import kotlinx.android.synthetic.main.activity_settings.*
import website.julianrosser.birthdays.AlarmsHelper
import website.julianrosser.birthdays.R
import website.julianrosser.birthdays.fragments.SettingsFragment

class SettingsActivity : BaseActivity() {

    var mTracker: Tracker? = null

    // To enable debug logging use: adb shell set prop log.tag.GAv4 DEBUG
    private val defaultTracker: Tracker?
        @Synchronized get() {
            if (mTracker == null) {
                val analytics = GoogleAnalytics.getInstance(this)
                mTracker = analytics.newTracker(R.xml.global_tracker)
            }
            return mTracker
        }

    public override fun onCreate(savedInstanceState: Bundle?) {
        setTheme()

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_settings)

        // hide drop shadow if running lollipop or higher
        if (Build.VERSION.SDK_INT >= 21) {
            findViewById<View>(R.id.drop_shadow_settings).visibility = View.GONE
        }

        // Pass Toolbar so it can be used like ActionBar
        setSupportActionBar(toolbar)

        // Create Settings Fragment
        fragmentManager.beginTransaction()
                .replace(R.id.content, SettingsFragment())
                .commit()

        // Obtain the shared Tracker instance.
        mTracker = defaultTracker
    }

    override fun onResume() {
        super.onResume()

        mTracker?.let {
            it.setScreenName("Settings")
            it.send(HitBuilders.ScreenViewBuilder().build())
        }
    }

    override fun onPause() {
        super.onPause()

        // Detect if Activity is closing, and recreate BirthdayListActivity to apply new theme
        if (this.isFinishing) setResult(BirthdayListActivity.RC_SETTINGS)
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

    override fun onStop() {
        super.onStop()
        AlarmsHelper.setAllNotificationAlarms(this)
    }

}
