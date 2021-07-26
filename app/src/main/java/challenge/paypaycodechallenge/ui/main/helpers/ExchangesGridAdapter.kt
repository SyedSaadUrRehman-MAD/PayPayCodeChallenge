package challenge.paypaycodechallenge.ui.main.helpers

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import challenge.paypaycodechallenge.R
import challenge.paypaycodechallenge.db.entities.Currency

class ExchangesGridAdapter(context: Context, selected: Currency, allCurrencies: List<Currency>) :
    RecyclerView.Adapter<ExchangesGridAdapter.ExchangeHolder>() {
    var selectedCurrency: Currency = selected
    var selectedAmount: Int = 1
    var currencies: List<Currency> = allCurrencies
    val context = context

    class ExchangeHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleName: TextView = itemView.findViewById(R.id.tvCurrencyName)
        val titleCode: TextView = itemView.findViewById(R.id.tvCurrencyCode)
        val exchngeValue: TextView = itemView.findViewById(R.id.tvConvertAmount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExchangeHolder {
        val inflator = LayoutInflater.from(context)
        return ExchangeHolder(inflator.inflate(R.layout.exchange_listitem, null, false))
    }

    override fun onBindViewHolder(holder: ExchangeHolder, position: Int) {
        holder.titleName.text = currencies.get(position).name
        holder.titleCode.text = currencies.get(position).currencyCode.toUpperCase()
        holder.exchngeValue.text = String.format(
            "%.4f",
            selectedAmount * currencies.get(position).exchangeRate / selectedCurrency.exchangeRate
        )
    }

    fun setSelected(currency: Currency) {
        selectedCurrency = currency
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return currencies.size
    }

    fun setAmount(parseInt: Int) {
        selectedAmount = parseInt
        notifyDataSetChanged()
    }

}