package challenge.paypaycodechallenge.ui.main.viewmodels

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import challenge.paypaycodechallenge.BuildConfig
import challenge.paypaycodechallenge.db.CurrencyDataSource
import challenge.paypaycodechallenge.db.entities.Currency
import challenge.paypaycodechallenge.models.GetCurrenciesResponse
import challenge.paypaycodechallenge.models.GetRatesResponse
import challenge.paypaycodechallenge.network.BasicAuthClient
import challenge.paypaycodechallenge.userpreferences.put
import challenge.paypaycodechallenge.utils.Constants
import challenge.paypaycodechallenge.utils.SingleLiveEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.HashMap

class LoaderViewModel(db: CurrencyDataSource, pref: SharedPreferences) : ViewModel() {
    private var mIsLoaded: SingleLiveEvent<Boolean> = SingleLiveEvent()
    var isLoaded: LiveData<Boolean> = mIsLoaded

    private var mRateList: MutableLiveData<HashMap<String, Float>> = MutableLiveData()
    var ratesList: LiveData<HashMap<String, Float>> = mRateList

    private var mCNameList: MutableLiveData<HashMap<String, String>> = MutableLiveData()
    var cNameList: LiveData<HashMap<String, String>> = mCNameList


    private val currencyPrefix: String = "Cname"
    private var appPreferences = pref
    private var appDb = db
    private var appPreferencesEditor = pref.edit()


    fun loadExchangeRates() {
        CoroutineScope(Dispatchers.IO).launch {
            val call = BasicAuthClient.create().getExchangeRates(BuildConfig.API_ACCESS_KEY)
            call.enqueue(object : Callback<GetRatesResponse> {
                override fun onFailure(call: Call<GetRatesResponse>, t: Throwable) {
                    Log.e("Load Exchange Rates", "failed $call")
                }

                override fun onResponse(
                    call: Call<GetRatesResponse>,
                    response: Response<GetRatesResponse>
                ) {
                    val ratesResponse: GetRatesResponse? = response.body()
                    if (ratesResponse != null && ratesResponse.success) {
                        Log.d("Load Exchange Rates", "response" + ratesResponse)
                        mRateList.value = ratesResponse.quotes
                        saveQuotes(ratesResponse)
                    } else
                        Log.d("Load Exchange Rates", "response is null"+ratesResponse)
                }
            })

        }
    }

    private fun saveQuotes(ratesResponse: GetRatesResponse) {
        appPreferencesEditor.put(
            Pair(
                Constants.KEY_LAST_REFRESHED,
                Calendar.getInstance().timeInMillis
            )
        )
        appPreferencesEditor.apply()
        parseAndSaveRates(ratesResponse.quotes)
    }

    private fun parseAndSaveRates(map: HashMap<String, Float>) {
        viewModelScope.launch {
            val it: MutableIterator<*> = map.entries.iterator()
            while (it.hasNext()) {
                val pair = it.next() as Map.Entry<String, Float>
                println(pair.key.substring(3) + " = " + pair.value)
                val code = pair.key.substring(3)
                var curr = appDb.getCurrencyFromCode(code)
                if (curr != null) {
                    curr.exchangeRate = pair.value
                    appDb.updateCurrency(curr)
                }

                appPreferencesEditor.put(Pair(pair.key.substring(3), pair.value));
                appPreferencesEditor.apply()
                it.remove() // avoids a ConcurrentModificationException
            }
            appPreferencesEditor.put(Pair(Constants.KEY_RATES_SAVED, true))
            appPreferencesEditor.apply()
            mIsLoaded.value = appPreferences.getBoolean(Constants.KEY_CURRENCY_SAVED, false)

        }

    }

    fun loadSupportedCurrencies() {
        CoroutineScope(Dispatchers.IO).launch {
            val call = BasicAuthClient.create().getSupportedCurrencies(BuildConfig.API_ACCESS_KEY)
            call.enqueue(object : Callback<GetCurrenciesResponse> {
                override fun onFailure(call: Call<GetCurrenciesResponse>, t: Throwable) {
                    Log.e("Load Currencies", "failed $call")
                }

                override fun onResponse(
                    call: Call<GetCurrenciesResponse>,
                    response: Response<GetCurrenciesResponse>
                ) {
                    val ratesResponse: GetCurrenciesResponse? = response.body()
                    if (ratesResponse != null && ratesResponse.success) {
                        Log.d("Load Currencies", "response" + ratesResponse)
                        mCNameList.value = ratesResponse.currencies
                        parseAndSaveCurrencies(ratesResponse.currencies)
                    } else
                        Log.d("Load Currencies", "response is null")
                }
            })

        }
    }

    private fun parseAndSaveCurrencies(map: HashMap<String, String>) {
        viewModelScope.launch {
            val it: MutableIterator<*> = map.entries.iterator()
            while (it.hasNext()) {
                val pair = it.next() as Map.Entry<String, String>
                println(pair.key.toString() + " = " + pair.value)

                var curr = Currency()
                curr.currencyCode = pair.key
                curr.name = pair.value
                appDb.addCurrency(curr)

                appPreferencesEditor.put(Pair(currencyPrefix + pair.key, pair.value));
                appPreferencesEditor.apply()
                it.remove() // avoids a ConcurrentModificationException
            }
            appPreferencesEditor.put(Pair(Constants.KEY_CURRENCY_SAVED, true))
            appPreferencesEditor.apply()
            mIsLoaded.value = appPreferences.getBoolean(Constants.KEY_RATES_SAVED, false)
        }
    }
}