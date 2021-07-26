package challenge.paypaycodechallenge.db.daos

import androidx.room.*
import challenge.paypaycodechallenge.db.entities.Currency
import challenge.paypaycodechallenge.db.tuples.CurrencyCodesTuple

@Dao
interface CurrencyDao {
    @Query("SELECT * FROM currency")
    fun getAll(): List<Currency>

    @Query(
        "SELECT * FROM currency WHERE currency_code LIKE :code"
    )
    fun findByCode(code: String): Currency

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg currencies: Currency)

    @Delete
    fun delete(currency: Currency)

    @Update
    fun update(vararg currency: Currency)

    @Query("SELECT currency_code FROM currency")
    fun getAllCodes(): List<String>
}