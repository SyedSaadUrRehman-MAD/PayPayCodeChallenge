package challenge.paypaycodechallenge.ui.main.viewmodels

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import challenge.paypaycodechallenge.db.entities.Currency
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class ExchangeCurrencyViewModelTest {
    @Mock
    var appPreferences: SharedPreferences? = null

    //Field appDb of type CurrencyDataSource - was not mocked since Mockito doesn't mock a Final class when 'mock-maker-inline' option is not set
    @Mock
    var appPreferencesEditor: SharedPreferences.Editor? = null

    @Mock
    var mCurrenciesList: MutableLiveData<List<Currency>>? = null

    @Mock
    var currenciesList: LiveData<List<Currency>>? = null

    @Mock
    var mCodesList: MutableLiveData<String>? = null

    @Mock
    var codesList: LiveData<String>? = null

    @Mock
    var mLastSelected: MutableLiveData<Currency>? = null

    @Mock
    var lastSelectedCurrency: LiveData<Currency>? = null

    @Mock
    var mBagOfTags: Map<String, Any>? = null

    @InjectMocks
    var exchangeCurrencyViewModel: ExchangeCurrencyViewModel? = null

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    @Throws(Exception::class)
    fun testGetCurrenciesList() {
        val result = exchangeCurrencyViewModel!!.currenciesList
        Assert.assertEquals(null, result)
    }


    @Test
    @Throws(Exception::class)
    fun testGetCodesList() {
        val result: LiveData<Array<String>> = exchangeCurrencyViewModel!!.codesList
        Assert.assertEquals(null, result)
    }

    @Test
    @Throws(Exception::class)
    fun testGetLastSelectedCurrency() {
        val result = exchangeCurrencyViewModel!!.lastSelectedCurrency
        Assert.assertEquals(null, result)
    }

    @Test
    @Throws(Exception::class)
    fun testGetLastUpdatedString() {
        val result = exchangeCurrencyViewModel!!.getLastUpdatedString()
        Assert.assertEquals("replaceMeWithExpectedResult", result)
    }

    @Test
    @Throws(Exception::class)
    fun testGetLastSelected() {
        exchangeCurrencyViewModel!!.getLastSelected()
    }

    @Test
    @Throws(Exception::class)
    fun testGetAllCurrencies() {
        exchangeCurrencyViewModel!!.getAllCurrencies()
    }

    @Test
    @Throws(Exception::class)
    fun testGetAllSupportedCurrenciesNames() {
        exchangeCurrencyViewModel!!.getAllSupportedCurrenciesNames()
    }

    @Test
    @Throws(Exception::class)
    fun testSetSelectedCurrencyCode() {
        exchangeCurrencyViewModel!!.setSelectedCurrencyCode("code")
    }

    @Test
    @Throws(Exception::class)
    fun testGetDefaultSelected() {
        val result = exchangeCurrencyViewModel!!.getDefaultSelected()
        Assert.assertEquals("USD", result.currencyCode)
    }
}