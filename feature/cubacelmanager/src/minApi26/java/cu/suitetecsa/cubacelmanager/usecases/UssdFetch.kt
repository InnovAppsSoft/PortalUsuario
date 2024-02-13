package cu.suitetecsa.cubacelmanager.usecases

import android.annotation.SuppressLint
import android.util.Log
import cu.suitetecsa.sdk.android.balance.FetchBalanceCallBack
import cu.suitetecsa.sdk.android.balance.consult.UssdRequest
import cu.suitetecsa.sdk.android.balance.response.Custom
import cu.suitetecsa.sdk.android.balance.response.UssdResponse
import cu.suitetecsa.sdk.android.kotlin.ussdFetch
import cu.suitetecsa.sdk.android.model.SimCard
import javax.inject.Inject

private const val TAG = "ExecuteUSSD API 26"

class UssdFetch @Inject constructor() {
    @SuppressLint("MissingPermission")
    operator fun invoke(simCard: SimCard, ussdCode: String, onRequest: () -> Unit, onFinish: (String) -> Unit) {
        simCard.ussdFetch(
            ussdCode,
            object : FetchBalanceCallBack {
                override fun onFailure(throwable: Throwable) {
                    onFinish(throwable.message.toString())
                }

                override fun onFetching(request: UssdRequest) {
                    onRequest()
                }

                override fun onSuccess(request: UssdRequest, response: UssdResponse) {
                    Log.d(TAG, "onSuccess: ${request.name}")
                    when (request) {
                        UssdRequest.CUSTOM -> {
                            Log.d(
                                TAG,
                                "onSuccess: ${Result.success((response as Custom).response)}"
                            )
                            onFinish(response.response)
                        }

                        else -> {}
                    }
                }
            }
        )
    }
}
