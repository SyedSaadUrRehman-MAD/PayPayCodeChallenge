package challenge.paypaycodechallenge.ui.main.activities

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.room.Room
import challenge.paypaycodechallenge.R
import challenge.paypaycodechallenge.db.AppDatabase
import challenge.paypaycodechallenge.db.CurrencyDataSource
import challenge.paypaycodechallenge.ui.main.fragments.ExchangeCurrencyFragment
import challenge.paypaycodechallenge.ui.main.helpers.VMFactory
import challenge.paypaycodechallenge.ui.main.viewmodels.LoaderViewModel
import challenge.paypaycodechallenge.userpreferences.AppPreferenceHelper
import challenge.paypaycodechallenge.utils.Constants

class ExchangeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, ExchangeCurrencyFragment.newInstance())
                    .commitNow()
        }
    }
}