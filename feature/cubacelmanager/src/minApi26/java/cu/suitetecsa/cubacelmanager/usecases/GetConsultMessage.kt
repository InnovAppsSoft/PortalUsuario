package cu.suitetecsa.cubacelmanager.usecases

import android.content.Context
import cu.suitetecsa.cubacelmanager.R
import cu.suitetecsa.sdk.android.balance.consult.UssdRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

internal class GetConsultMessage @Inject constructor(
    @ApplicationContext private val context: Context
) {
    operator fun invoke(request: UssdRequest, customType: CustomConsult = CustomConsult.TopUpBalance): String {
        val typeConsult = when (request) {
            UssdRequest.BONUS_BALANCE -> context.getString(R.string.bonus)
            UssdRequest.CUSTOM -> {
                when (customType) {
                    CustomConsult.BuyingPackage -> context.getString(R.string.buying_package)
                    CustomConsult.TopUpBalance -> context.getString(R.string.topping_balance)
                    is CustomConsult.TurnUsageBasedPricing -> {
                        if (customType.active) {
                            context.getString(R.string.turn_on_ubp)
                        } else {
                            context.getString(R.string.turn_off_ubp)
                        }
                    }
                }
            }
            UssdRequest.DATA_BALANCE -> context.getString(R.string.data)
            UssdRequest.MESSAGES_BALANCE -> context.getString(R.string.sms)
            UssdRequest.PRINCIPAL_BALANCE -> context.getString(R.string.balance_credit)
            UssdRequest.VOICE_BALANCE -> context.getString(R.string.voice)
        }
        return context.getString(R.string.requesting, typeConsult)
    }
}

internal sealed class CustomConsult {
    data object TopUpBalance : CustomConsult()
    data class TurnUsageBasedPricing(val active: Boolean) : CustomConsult()
    data object BuyingPackage : CustomConsult()
}
