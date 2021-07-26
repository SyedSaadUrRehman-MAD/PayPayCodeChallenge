package challenge.paypaycodechallenge.ui.main

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.room.Room
import challenge.paypaycodechallenge.db.AppDatabase
import challenge.paypaycodechallenge.db.CurrencyDataSource
import challenge.paypaycodechallenge.ui.main.helpers.VMFactory
import challenge.paypaycodechallenge.ui.main.viewmodels.LoaderViewModel
import challenge.paypaycodechallenge.utils.getOrAwaitValue
import junit.framework.TestCase
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.*
import java.io.IOException

@RunWith(JUnit4::class)
class LoadVMTest : TestCase() {
    @Mock
    lateinit var pref: SharedPreferences

    @Mock
    var db: AppDatabase? = null

    @Mock
    lateinit var dataSource: CurrencyDataSource

    @Mock
    lateinit var prefEditor: SharedPreferences.Editor

    @Mock
    var log: Log? = null

    @Mock
    var mApplication: Application? = null

    @InjectMocks
    var loaderViewModel: LoaderViewModel? = null

    @get:Rule
    public val rule = InstantTaskExecutorRule()

    @Before
    public override fun setUp() {
        mApplication = Mockito.mock(Application::class.java)
        val context = Mockito.mock(Context::class.java)
        pref = Mockito.mock(SharedPreferences::class.java)
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).allowMainThreadQueries()
            .build()
        dataSource = CurrencyDataSource(db!!.currencyDao())
        log = Mockito.mock(Log::class.java)
        prefEditor = Mockito.mock(SharedPreferences.Editor::class.java)
        Mockito.`when`(
            context.getSharedPreferences(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyInt()
            )
        ).thenReturn(pref)
        Mockito.`when`(
            context.getSharedPreferences(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyInt()
            ).edit()
        ).thenReturn(prefEditor)
        loaderViewModel = VMFactory(dataSource!!, pref).create(
            LoaderViewModel::class.java
        )
    }

    @Test
    @Throws(Exception::class)
    fun testLoadExchangeRatesInFiveSeconds() {
        loaderViewModel!!.loadExchangeRates()
        val result = loaderViewModel!!.ratesList.getOrAwaitValue().get("USDUSD")
        assertTrue(result != null)
    }

    @Test
    @Throws(Exception::class)
    fun testLoadSupportedCurrenciesInFiveSeconds() {
        loaderViewModel!!.loadSupportedCurrencies()
        val result = loaderViewModel!!.cNameList.getOrAwaitValue().get("USD")
        assertTrue(result != null)
    }


    @Test
    fun isLoaded() {
        testLoadSupportedCurrenciesInFiveSeconds()
        testLoadExchangeRatesInFiveSeconds()
        val result = loaderViewModel!!.isLoaded.getOrAwaitValue()
        Assert.assertTrue(result)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db!!.close()
    }
}