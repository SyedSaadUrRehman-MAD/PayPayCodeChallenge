package challenge.paypaycodechallenge.db.entities

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Currency {
    @NonNull
    @ColumnInfo(name = "currency_code")
    @PrimaryKey
    lateinit var currencyCode: String

    @ColumnInfo(name = "exchange_rate")
    var exchangeRate: Float = 0.0f

    @ColumnInfo(name = "curr_name")
    lateinit var name: String
}