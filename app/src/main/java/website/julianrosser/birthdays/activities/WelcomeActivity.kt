package website.julianrosser.birthdays.activities

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.common.SignInButton
import kotlinx.android.synthetic.main.activity_welcome.*
import website.julianrosser.birthdays.R

class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme()
        setContentView(R.layout.activity_welcome)

        welcomeButtonGoogleSignIn.setOnClickListener { onGoogleSignInClicked() }
        welcomeButtonJson.setOnClickListener { onContinueClicked() }
    }

    private fun onGoogleSignInClicked() {

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
            else -> R.style.PinkTheme
        })
    }
}