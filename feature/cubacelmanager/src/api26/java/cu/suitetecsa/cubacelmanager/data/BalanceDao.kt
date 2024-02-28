package cu.suitetecsa.cubacelmanager.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.IGNORE
import androidx.room.Query
import androidx.room.Update
import cu.suitetecsa.cubacelmanager.data.entyties.Balance
import kotlinx.coroutines.flow.Flow

@Dao
internal interface BalanceDao {
    @Query("SELECT * FROM balance_table ORDER BY subscriptionID ASC")
    fun getBalances(): Flow<List<Balance>>

    @Query("SELECT * FROM balance_table WHERE subscriptionID = :subscriptionID")
    suspend fun getBalance(subscriptionID: Int): Balance?

    @Insert(onConflict = IGNORE)
    suspend fun addBalance(balance: Balance)

    @Update
    suspend fun updateBalance(balance: Balance)
}
