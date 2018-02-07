package website.julianrosser.birthdays.activities

import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import website.julianrosser.birthdays.R

class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme()
        setContentView(R.layout.activity_welcome)
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