package challenge.paypaycodechallenge.ui.main.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import challenge.paypaycodechallenge.R
import challenge.paypaycodechallenge.db.AppDatabase
import challenge.paypaycodechallenge.db.CurrencyDataSource
import challenge.paypaycodechallenge.db.entities.Currency
import challenge.paypaycodechallenge.ui.main.helpers.ExchangesGridAdapter
import challenge.paypaycodechallenge.ui.main.helpers.VMFactory
import challenge.paypaycodechallenge.ui.main.viewmodels.ExchangeCurrencyViewModel
import challenge.paypaycodechallenge.userpreferences.AppPreferenceHelper
import challenge.paypaycodechallenge.utils.Constants

class ExchangeCurrencyFragment : Fragment(), AdapterView.OnItemSelectedListener, TextWatcher {

    companion object {
        fun newInstance() = ExchangeCurrencyFragment()
    }

    private lateinit var pref: SharedPreferences
    private lateinit var viewModel: ExchangeCurrencyViewModel
    private lateinit var rvExchangeRates: RecyclerView
    private lateinit var tvLastUpdated: TextView
    private lateinit var etAmount: EditText
    private lateinit var spCurrList: Spinner
    private lateinit var exchangesGridAdapter: ExchangesGridAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initViews();
        val appContext = activity?.applicationContext
        val db = appContext?.let {
            Room.databaseBuilder(
                it,
                AppDatabase::class.java, Constants.DB_NAME
            ).allowMainThreadQueries().build()
        }
        pref = activity?.let { AppPreferenceHelper.appPreferences(it) }!!

        val ds = db?.let { CurrencyDataSource(it.currencyDao()) }
        viewModel = pref?.let {
            ds?.let { it1 ->
                VMFactory(
                    it1,
                    it
                ).create(ExchangeCurrencyViewModel::class.java)
            }
        }!!


        //populating spinner
        viewModel.codesList.observe(requireActivity(), Observer {
            spCurrList.adapter = ArrayAdapter<String>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                it
            )
        })

        //populating list
        viewModel.currenciesList.observe(requireActivity(), Observer {
            val currenciesList = it
            viewModel.lastSelectedCurrency.observe(requireActivity(), Observer {
                if (it != null) {
                    val selected = it
                    populateGrid(selected, currenciesList)
                } else
                    Log.d("PayPay", "Nothing selecetd before")
            })
        })

        //fetching to be observed
        viewModel.getAllSupportedCurrenciesNames()
        viewModel.getAllCurrencies()
        viewModel.getLastSelected()
    }

    private fun populateGrid(selectedCurrency: Currency, allCurrencies: List<Currency>) {
        exchangesGridAdapter =
            ExchangesGridAdapter(requireActivity(), selectedCurrency, allCurrencies)
        rvExchangeRates.adapter = exchangesGridAdapter

        tvLastUpdated.text = viewModel.getLastUpdatedString()
    }

    private fun initViews() {
        etAmount = requireView().findViewById(R.id.etAmount)
        spCurrList = requireView().findViewById(R.id.spCurrList)
        rvExchangeRates = requireView().findViewById(R.id.rvExchangeRates)
        tvLastUpdated = requireView().findViewById(R.id.tvLastUpdated)

        spCurrList.onItemSelectedListener = this
        etAmount.addTextChangedListener(this)
    }


    //region Spinner Item Selected Implementation
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (!etAmount.text.equals("")) {
            etAmount.setText("1")
            exchangesGridAdapter?.setAmount(Integer.parseInt(etAmount.text.toString()))
        }
        viewModel.setSelectedCurrencyCode(parent!!.getItemAtPosition(position) as String)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        Log.d("spinneritemselected", "nothing selected")
    }
    //endregion

    //region TextWatcher Implementation


    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (!etAmount.text.equals(""))
            exchangesGridAdapter?.setAmount(Integer.parseInt(etAmount.text.toString()))
    }

    override fun afterTextChanged(s: Editable?) {
        if (etAmount.text.equals("")) {
            etAmount.setText("1")
        } else
            exchangesGridAdapter?.setAmount(Integer.parseInt(etAmount.text.toString()))
    }
    //endregion

}