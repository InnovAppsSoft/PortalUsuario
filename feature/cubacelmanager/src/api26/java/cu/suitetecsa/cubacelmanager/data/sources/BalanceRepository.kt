package cu.suitetecsa.cubacelmanager.data.sources

import cu.suitetecsa.cubacelmanager.data.BalanceDao
import cu.suitetecsa.cubacelmanager.data.entyties.toDomain
import cu.suitetecsa.cubacelmanager.domain.model.Balance
import cu.suitetecsa.cubacelmanager.domain.model.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class BalanceRepository(
    private val balanceDao: BalanceDao
) {
    fun getBalances(): Flow<List<Balance>> = balanceDao.getBalances().map { balances ->
        balances.map { balance ->
            balance.toDomain()
        }
    }

    suspend fun getBalance(slotIndex: Int): Balance? = balanceDao.getBalance(slotIndex)?.toDomain()

    suspend fun updateBalance(balance: Balance) {
        getBalance(balance.subscriptionID)?.let {
            balanceDao.updateBalance(balance.toEntity())
        } ?: run {
            balanceDao.addBalance(balance.toEntity())
        }
    }
}
