package website.julianrosser.birthdays.activities

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_welcome.*
import website.julianrosser.birthdays.R

class WelcomeActivity : GoogleSignInActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme()
        setContentView(R.layout.activity_welcome)

        setUpSignInButton()
        welcomeButtonJson.setOnClickListener { onContinueClicked() }
    }

    private fun setUpSignInButton() {
        setUpGoogleSignInButton(welcomeButtonGoogleSignIn, object : GoogleSignInListener {
            override fun onLogin(firebaseUser: FirebaseUser) {
                Snackbar.make(welcomeButtonGoogleSignIn, "Already signed in, GOTO main activity", Snackbar.LENGTH_SHORT).show()
            }

            override fun onGoogleFailure(message: String) {
                Snackbar.make(welcomeButtonGoogleSignIn, "onGoogleFailure: $message", Snackbar.LENGTH_SHORT).show()
            }

            override fun onFirebaseFailure(message: String) {
                Snackbar.make(welcomeButtonGoogleSignIn, "Not signed in, new or signed out user: $message", Snackbar.LENGTH_SHORT).show()
            }
        })
    }

    private fun onContinueClicked() {
        startActivity(Intent(this, BirthdayListActivity::class.java))
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
}