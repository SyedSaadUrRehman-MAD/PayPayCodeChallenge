package challenge.paypaycodechallenge.db.tuples

import androidx.room.ColumnInfo

data class CurrencyCodesTuple(
    @ColumnInfo(name = "currency_code") val code: String?
)