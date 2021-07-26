package challenge.paypaycodechallenge.ui.main.viewmodels

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import challenge.paypaycodechallenge.db.CurrencyDataSource
import challenge.paypaycodechallenge.db.entities.Currency
import challenge.paypaycodechallenge.userpreferences.put
import challenge.paypaycodechallenge.utils.Constants
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ExchangeCurrencyViewModel(db: CurrencyDataSource, pref: SharedPreferences) : ViewModel() {
    private var appPreferences = pref
    private var appDb = db
    private var appPreferencesEditor = pref.edit()

    private var mCurrenciesList: MutableLiveData<List<Currency>> = MutableLiveData()
    var currenciesList: LiveData<List<Currency>> = mCurrenciesList

    private var mCodesList: MutableLiveData<Array<String>> = MutableLiveData()
    var codesList: LiveData<Array<String>> = mCodesList

    private var mLastSelected: MutableLiveData<Currency> = MutableLiveData()
    var lastSelectedCurrency: LiveData<Currency> = mLastSelected

    fun getLastUpdatedString(): String {
        val date = Date()
        date.time = appPreferences.getLong(Constants.KEY_LAST_REFRESHED, 0L)
        val sdf = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy")
        return sdf.format(date)
    }

    private fun setLastSelectedCurrency(currency: Currency) {
        mLastSelected.value = currency
        val gson = Gson();
        val json: String = gson.toJson(currency);
        appPreferencesEditor.put(Pair(Constants.KEY_LAST_SELECTED, json))
        appPreferencesEditor.apply()
    }

    fun getLastSelected() {
        val gson = Gson();
        val jsonString =
            appPreferences.getString(Constants.KEY_LAST_SELECTED, gson.toJson(getDefaultSelected()))
        val currency = gson.fromJson(jsonString, Currency::class.java)
        mLastSelected.value = currency
    }

    fun getAllCurrencies() {
        viewModelScope.launch {
            mCurrenciesList.value = appDb.getAllCurrencies()
        }
    }

    fun getAllSupportedCurrenciesNames() {
        viewModelScope.launch {
            val allnames = appDb.getAllSupportedNames()
            val size = allnames.size
            var arr = Array<String>(size){""}
            for(i in 0..size-1)
            {
                arr[i] = allnames.get(i)
            }
            mCodesList.value = arr
        }
    }

    fun setSelectedCurrencyCode(code: String) {
        viewModelScope.launch {
            setLastSelectedCurrency(appDb.getCurrencyFromCode(code))
        }
    }

    fun getDefaultSelected(): Currency {
        val c = Currency()
        c.currencyCode = "USD"
        c.exchangeRate = 1.0f
        c.name = "United States Dollar"
        return c
    }
}