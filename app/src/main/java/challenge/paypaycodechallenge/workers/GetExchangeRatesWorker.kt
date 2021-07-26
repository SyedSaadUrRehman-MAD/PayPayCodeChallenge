package challenge.paypaycodechallenge.workers

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import challenge.paypaycodechallenge.BuildConfig
import challenge.paypaycodechallenge.db.AppDatabase
import challenge.paypaycodechallenge.db.CurrencyDataSource
import challenge.paypaycodechallenge.models.GetRatesResponse
import challenge.paypaycodechallenge.network.BasicAuthClient
import challenge.paypaycodechallenge.userpreferences.AppPreferenceHelper
import challenge.paypaycodechallenge.userpreferences.put
import challenge.paypaycodechallenge.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.HashMap

class GetExchangeRatesWorker(
    private val context: Context,
    private val workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {
    private lateinit var appDb: CurrencyDataSource
    private lateinit var pref: SharedPreferences
    private lateinit var appPreferencesEditor: SharedPreferences.Editor

    override suspend fun doWork(): Result {
        pref = AppPreferenceHelper.appPreferences(context)
        appPreferencesEditor = pref.edit()
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, Constants.DB_NAME
        ).allowMainThreadQueries().build()

        appDb = CurrencyDataSource(db.currencyDao())

        return try {
            val call = BasicAuthClient.create().getExchangeRates(BuildConfig.API_ACCESS_KEY)
            call.enqueue(object : Callback<GetRatesResponse> {
                override fun onFailure(call: Call<GetRatesResponse>, t: Throwable) {
                    Log.e("rates worker", "failed $call")
                }

                override fun onResponse(
                    call: Call<GetRatesResponse>,
                    response: Response<GetRatesResponse>
                ) {
                    val ratesResponse: GetRatesResponse? = response.body()
                    if (ratesResponse != null && ratesResponse.success) {
                        Log.d("rates worker", "response" + ratesResponse)
                        CoroutineScope(Dispatchers.Main).launch {
                            saveQuotes(ratesResponse)
                        }
                    } else
                        Log.d("rates worker", "response is null")
                }
            })
            return Result.success()
        } catch (throwable: Throwable) {
            Log.e("rates worker", "Error applying blur")
            Result.failure()
        }
    }

    private suspend fun saveQuotes(ratesResponse: GetRatesResponse) {
        appPreferencesEditor.put(
            Pair(
                Constants.KEY_LAST_REFRESHED,
                Calendar.getInstance().timeInMillis
            )
        )
        appPreferencesEditor.apply()
        parseAndSaveRates(ratesResponse.quotes)
    }

    private suspend fun parseAndSaveRates(map: HashMap<String, Float>) {
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
    }

}