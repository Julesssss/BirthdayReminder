package website.julianrosser.birthdays.activities

import android.os.Bundle
import android.preference.PreferenceManager
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_help.*
import website.julianrosser.birthdays.R
import website.julianrosser.birthdays.fragments.ImportContactFragment

class ImportContactsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme()
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_import_contacts)
        setSupportActionBar(toolbar)

        // Show backToBirthdays button on toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        intent?.extras?.getStringArrayList(BIRTHDAYS_ARRAY_KEY)?.let {

            // Create new RecyclerListFragment
            val recyclerListFragment = ImportContactFragment.newInstance()
            recyclerListFragment.setBirthdayNames(it)

            supportFragmentManager.beginTransaction()
                    .add(R.id.container, recyclerListFragment)
                    .commit()
            recyclerListFragment.retainInstance = true
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        if (id == android.R.id.home) {
            backToBirthdays()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        backToBirthdays()
    }

    private fun backToBirthdays() {
        finish()
    }

    companion object {
        var BIRTHDAYS_ARRAY_KEY = "BIRTHDAYS_ARRAY"
    }
}
