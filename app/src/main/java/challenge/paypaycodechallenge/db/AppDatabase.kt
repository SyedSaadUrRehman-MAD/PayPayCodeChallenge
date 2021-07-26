package challenge.paypaycodechallenge.db

import androidx.room.Database
import androidx.room.RoomDatabase
import challenge.paypaycodechallenge.db.daos.CurrencyDao
import challenge.paypaycodechallenge.db.entities.Currency

@Database(entities = arrayOf(Currency::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun currencyDao(): CurrencyDao
}