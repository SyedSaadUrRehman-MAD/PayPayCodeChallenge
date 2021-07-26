package challenge.paypaycodechallenge.ui.main.activities

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.work.*
import challenge.paypaycodechallenge.R
import challenge.paypaycodechallenge.db.AppDatabase
import challenge.paypaycodechallenge.db.CurrencyDataSource
import challenge.paypaycodechallenge.ui.main.helpers.VMFactory
import challenge.paypaycodechallenge.ui.main.viewmodels.LoaderViewModel
import challenge.paypaycodechallenge.userpreferences.AppPreferenceHelper
import challenge.paypaycodechallenge.utils.Constants
import challenge.paypaycodechallenge.workers.GetExchangeRatesWorker
import java.util.*
import java.util.concurrent.TimeUnit

class SplashActivity : AppCompatActivity() {
    private lateinit var viewModel: LoaderViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val pref: SharedPreferences = AppPreferenceHelper.appPreferences(this)

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, Constants.DB_NAME
        ).allowMainThreadQueries().build()

        val ds = CurrencyDataSource(db.currencyDao())
        viewModel = VMFactory(ds, pref).create(LoaderViewModel::class.java)

        val currenciesSaved: Boolean = pref.getBoolean(Constants.KEY_CURRENCY_SAVED, false)
        val rateSaved: Boolean = pref.getBoolean(Constants.KEY_RATES_SAVED, false)

        if (!currenciesSaved) {
            viewModel.loadSupportedCurrencies()
            viewModel.cNameList.observe(this, Observer {
                if (!rateSaved) {
                    Handler().postDelayed(Runnable {
                        viewModel.loadExchangeRates()
                    }, 2000)
                }
            })
        } else {
            if (!rateSaved) {
                viewModel.loadExchangeRates()
            }
        }

        if (!currenciesSaved || !rateSaved)
            viewModel.isLoaded.observe(this, Observer {
                if (it) {
                    navigateAndEnquePeriodicRequest()
                }
            })
        else {
            //everything is preloaded
            navigateAndEnquePeriodicRequest()
        }
    }

    private fun navigateAndEnquePeriodicRequest() {
        startActivity(Intent(this, ExchangeActivity::class.java))
        createPeriodicWorkRequest()
        finish()
    }

    private fun createPeriodicWorkRequest() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()
        // 1
        val ratesWorker = PeriodicWorkRequestBuilder<GetExchangeRatesWorker>(
            30, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .addTag("GetExchangeRatesWork")
            .build()
        // 2
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "GetExchangeRatesWork",
            ExistingPeriodicWorkPolicy.KEEP,
            ratesWorker
        )
        observeWork(ratesWorker.id)
    }

    private fun observeWork(id: UUID) {
        // 1
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(id)
            .observe(this, { info ->
                // 2
                if (info != null && info.state.isFinished) {
//                    hideLottieAnimation()
//                    activityHomeBinding.downloadLayout.visibility = View.VISIBLE
                    // 3
//                    val uriResult = info.outputData.getString("IMAGE_URI")
//                    if (uriResult != null) {
//                        showDownloadedImage(uriResult.toUri())
//                    }
                }
            })
    }
}