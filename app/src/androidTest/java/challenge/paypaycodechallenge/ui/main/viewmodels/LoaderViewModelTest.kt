package challenge.paypaycodechallenge.ui.main.viewmodels

import android.content.SharedPreferences
import android.util.Log
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import challenge.paypaycodechallenge.db.AppDatabase
import challenge.paypaycodechallenge.db.CurrencyDataSource
import challenge.paypaycodechallenge.ui.main.helpers.VMFactory
import challenge.paypaycodechallenge.userpreferences.AppPreferenceHelper
import challenge.paypaycodechallenge.utils.getOrAwaitValue
import junit.framework.TestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*
import org.junit.Rule
import org.junit.runner.RunWith
import androidx.arch.core.executor.testing.InstantTaskExecutorRule


@RunWith(AndroidJUnit4::class)
class LoaderViewModelTest {
    lateinit var pref: SharedPreferences
    var db: AppDatabase? = null
    lateinit var dataSource: CurrencyDataSource
    lateinit var prefEditor: SharedPreferences.Editor
    var loaderViewModel: LoaderViewModel? = null
    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        pref = AppPreferenceHelper.appPreferences(context)
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).allowMainThreadQueries()
            .build()
        dataSource = CurrencyDataSource(db!!.currencyDao())
        prefEditor = pref.edit()
        loaderViewModel = VMFactory(dataSource!!, pref).create(
            LoaderViewModel::class.java
        )
    }


    @Test
    fun isLoaded() {
        loadExchangeRates()
        loadSupportedCurrencies()
        val result = loaderViewModel!!.isLoaded.getOrAwaitValue()
        assertTrue(result)
    }

    @Test
    fun getRatesList() {
    }

    @Test
    fun setRatesList() {
    }

    @Test
    fun getCNameList() {
    }

    @Test
    fun setCNameList() {
    }

    @Test
    fun loadExchangeRates() {
        loaderViewModel!!.loadExchangeRates()
        val result = loaderViewModel!!.ratesList.getOrAwaitValue().get("USDUSD")
        TestCase.assertTrue(result != null)
    }

    @Test
    fun loadSupportedCurrencies() {
        loaderViewModel!!.loadSupportedCurrencies()
        val result = loaderViewModel!!.cNameList.getOrAwaitValue().get("USD")
        TestCase.assertTrue(result != null)
    }


    @After
    fun tearDown() {
        db!!.close()
    }
}