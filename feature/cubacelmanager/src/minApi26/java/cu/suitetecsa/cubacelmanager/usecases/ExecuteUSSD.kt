package cu.suitetecsa.cubacelmanager.usecases

import android.annotation.SuppressLint
import cu.suitetecsa.sdk.android.balance.ConsultBalanceCallBack
import cu.suitetecsa.sdk.android.balance.consult.UssdRequest
import cu.suitetecsa.sdk.android.balance.response.Custom
import cu.suitetecsa.sdk.android.balance.response.UssdResponse
import cu.suitetecsa.sdk.android.kotlin.ussdExecute
import cu.suitetecsa.sdk.android.model.SimCard
import javax.inject.Inject

class ExecuteUSSD @Inject constructor() {
    @SuppressLint("MissingPermission")
    operator fun invoke(simCard: SimCard, ussdCode: String, onRequest: () -> Unit, onFinish: (Result<String>) -> Unit) {
        simCard.ussdExecute(
            ussdCode,
            object : ConsultBalanceCallBack {
                override fun onFailure(throwable: Throwable) {
                    onFinish(Result.failure(throwable))
                }

                override fun onRequesting(request: UssdRequest) {
                    onRequest()
                }

                override fun onSuccess(request: UssdRequest, response: UssdResponse) {
                    when (request) {
                        UssdRequest.CUSTOM -> {
                            onFinish(Result.success((response as Custom).response))
                        }

                        else -> {}
                    }
                }
            }
        )
    }
}
