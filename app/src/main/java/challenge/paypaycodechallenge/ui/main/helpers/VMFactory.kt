package challenge.paypaycodechallenge.ui.main.helpers

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import challenge.paypaycodechallenge.db.CurrencyDataSource

class VMFactory(val db: CurrencyDataSource, val pref: SharedPreferences) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(CurrencyDataSource::class.java, SharedPreferences::class.java)
            .newInstance(db, pref)
    }
}