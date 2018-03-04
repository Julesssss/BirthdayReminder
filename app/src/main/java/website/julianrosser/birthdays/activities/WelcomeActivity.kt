package website.julianrosser.birthdays.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.util.Log
import com.google.android.gms.analytics.GoogleAnalytics
import com.google.android.gms.analytics.HitBuilders
import com.google.android.gms.analytics.Tracker
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_welcome.*
import website.julianrosser.birthdays.Preferences
import website.julianrosser.birthdays.R
import website.julianrosser.birthdays.database.DatabaseHelper

class WelcomeActivity : GoogleSignInActivity() {

    private var mTracker: Tracker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        if (Preferences.shouldShowWelcomeScreen(this)) {

            setTheme()
            setContentView(R.layout.activity_welcome)

            setUpSignInButton()
            welcomeButtonJson.setOnClickListener { onContinueClicked() }

            mTracker = getDefaultTracker()

//        } else {
//            startActivity(Intent(applicationContext, BirthdayListActivity::class.java))
//            finish()
//        }
    }

    private fun setUpSignInButton() {
        setUpGoogleSignInButton(welcomeButtonGoogleSignIn, object : GoogleSignInListener {

            override fun onLogin(firebaseUser: FirebaseUser) {

                // TODO: JSON data exists ONLY ONCE!!!!!!!!!
                if (true) {
                    migratejsonBirthdays(firebaseUser)
                } else {
                    handleLogin()
                }
            }

            override fun onGoogleFailure(message: String) {
                Log.i(javaClass.simpleName, "onGoogleFailure: $message")
            }

            override fun onFirebaseFailure(message: String) {
                Log.i(javaClass.simpleName, "New or signed out user: $message")
            }
        })
    }

    private fun migratejsonBirthdays(firebaseUser: FirebaseUser) {
        DatabaseHelper().migrateJsonToFirebase(applicationContext, firebaseUser, object : DatabaseHelper.MigrateUsersCallback {
            override fun onSuccess(migratedCount: Int) {
                Snackbar.make(welcomeButtonGoogleSignIn, "Migrated $migratedCount users!", Snackbar.LENGTH_SHORT).show()
                handleLogin()
            }

            override fun onFailure(message: String?) {
                Snackbar.make(welcomeButtonGoogleSignIn, "ERROR migrating: $message", Snackbar.LENGTH_SHORT).show()
                handleLogin()
            }
        })
    }

    private fun handleLogin() {
        startActivity(Intent(applicationContext, BirthdayListActivity::class.java))
        mTracker?.send(HitBuilders.EventBuilder()
                .setCategory("Action")
                .setAction("Welcome--Logged In")
                .build())
        Preferences.setShouldShowWelcomeScreen(applicationContext, false)
        finish()
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