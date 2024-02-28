package cu.suitetecsa.cubacelmanager.data.sources

import androidx.room.Database
import androidx.room.RoomDatabase
import cu.suitetecsa.cubacelmanager.data.BalanceDao
import cu.suitetecsa.cubacelmanager.data.entyties.Balance

@Database(entities = [Balance::class], version = 1, exportSchema = false)
internal abstract class BalanceDb : RoomDatabase() {
    abstract val balanceDao: BalanceDao
}
