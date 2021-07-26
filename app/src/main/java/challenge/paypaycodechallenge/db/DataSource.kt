package challenge.paypaycodechallenge.db

import challenge.paypaycodechallenge.db.daos.CurrencyDao
import challenge.paypaycodechallenge.db.entities.Currency

class CurrencyDataSource(
    private val db: CurrencyDao
) {
    suspend fun updateCurrency(currency: Currency) = db.update(currency)
    suspend fun addCurrency(currency: Currency) = db.insert(currency)
    suspend fun getCurrencyFromCode(code:String) = db.findByCode(code)
    suspend fun getAllCurrencies() = db.getAll()
    suspend fun getAllSupportedNames() = db.getAllCodes()
}