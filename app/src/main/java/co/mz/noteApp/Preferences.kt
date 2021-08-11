package co.mz.noteApp

import android.content.Context
import androidx.preference.PreferenceManager

class Preferences(context: Context?) {

    companion object {
        private const val DARK_STATUS = "themes"
    }

    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    var darkMode = preferences.getString(DARK_STATUS, "2")
        set(value) = preferences.edit().putString(DARK_STATUS, value).apply()

}