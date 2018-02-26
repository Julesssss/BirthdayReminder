package website.julianrosser.birthdays.activities

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import com.google.android.gms.analytics.GoogleAnalytics
import com.google.android.gms.analytics.HitBuilders
import com.google.android.gms.analytics.Tracker
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_welcome.*
import website.julianrosser.birthdays.Preferences
import website.julianrosser.birthdays.R

class WelcomeActivity : GoogleSignInActivity() {

    private var mTracker: Tracker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme()
        setContentView(R.layout.activity_welcome)

        setUpSignInButton()
        welcomeButtonJson.setOnClickListener { onContinueClicked() }

        mTracker = getDefaultTracker()
    }

    private fun setUpSignInButton() {
        setUpGoogleSignInButton(welcomeButtonGoogleSignIn, object : GoogleSignInListener {

            override fun onLogin(firebaseUser: FirebaseUser) {
                Snackbar.make(welcomeButtonGoogleSignIn, "Already signed in, GOTO main activity", Snackbar.LENGTH_SHORT).show()
                startActivity(Intent(applicationContext, BirthdayListActivity::class.java))
                mTracker?.send(HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("Welcome--Logged In")
                        .build())
                Preferences.setShouldShowWelcomeScreen(applicationContext, false)
                finish()
            }

            override fun onGoogleFailure(message: String) {
                Snackbar.make(welcomeButtonGoogleSignIn, "onGoogleFailure: $message", Snackbar.LENGTH_SHORT).show()
            }

            override fun onFirebaseFailure(message: String) {
                Snackbar.make(welcomeButtonGoogleSignIn, "New or signed out user: $message", Snackbar.LENGTH_SHORT).show()
            }
        })
    }

    private fun onContinueClicked() {
        startActivity(Intent(this, BirthdayListActivity::class.java))
        Preferences.setShouldShowWelcomeScreen(this, false)

        mTracker?.send(HitBuilders.EventBuilder()
                .setCategory("Action")
                .setAction("Welcome--Continue")
                .build())
        finish()
    }

    private fun setTheme() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        setTheme(when (prefs.getString(resources.getString(R.string.pref_theme_key), "")) {
            "0" -> R.style.BlueTheme
            "1" -> R.style.PinkTheme
            "2" -> R.style.GreenTheme
            else -> R.style.BlueTheme
        })
    }

    @Synchronized
    fun getDefaultTracker(): Tracker? {
        if (mTracker == null) {
            val analytics = GoogleAnalytics.getInstance(this)
            mTracker = analytics.newTracker(R.xml.global_tracker)
        }
        return mTracker
    }
}