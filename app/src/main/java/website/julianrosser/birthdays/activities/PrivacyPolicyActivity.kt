package website.julianrosser.birthdays.activities

import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.TextView
import com.google.android.gms.analytics.GoogleAnalytics
import com.google.android.gms.analytics.HitBuilders
import com.google.android.gms.analytics.Tracker
import kotlinx.android.synthetic.main.activity_privacy_policy.*
import kotlinx.android.synthetic.main.content_privacy_policy.*
import website.julianrosser.birthdays.R
import website.julianrosser.birthdays.Utils

class PrivacyPolicyActivity : BaseActivity() {

    private var mTracker: Tracker? = null

    private val defaultTracker: Tracker
        @Synchronized get() {
            if (mTracker == null) {
                val analytics = GoogleAnalytics.getInstance(this)
                mTracker = analytics.newTracker(R.xml.global_tracker)
            }
            return mTracker!!
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_policy)

        setPrivacyTitleColour(textPrivacySummary)
        setPrivacyTitleColour(textPrivacyFull)

        // Set up toolbar reference
        setSupportActionBar(toolbar)

        // Show home button on toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Obtain the shared Tracker instance.
        mTracker = defaultTracker

        mTracker?.send(HitBuilders.EventBuilder()
                .setCategory("Action")
                .setAction("Privacy Policy Activity")
                .build())
    }

    private fun setPrivacyTitleColour(textPrivacySummary: TextView) {
        val textColor = Utils.getHighlightColor(applicationContext)
        textPrivacySummary.setTextColor(resources.getColor(textColor))
    }

    override fun onResume() {
        super.onResume()
        mTracker?.let {
            it.setScreenName("Privacy Policy")
            it.send(HitBuilders.ScreenViewBuilder().build())
        }
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

}
