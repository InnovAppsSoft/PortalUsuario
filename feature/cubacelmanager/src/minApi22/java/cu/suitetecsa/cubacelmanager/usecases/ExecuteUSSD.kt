package cu.suitetecsa.cubacelmanager.usecases

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import cu.suitetecsa.sdk.android.kotlin.makeCall
import cu.suitetecsa.sdk.android.model.SimCard
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ExecuteUSSD @Inject constructor(@ApplicationContext private val context: Context) {
    @RequiresPermission(Manifest.permission.CALL_PHONE)
    operator fun invoke(simCard: SimCard, ussdCode: String, onRequest: () -> Unit, function: () -> Unit) {
        onRequest()
        function()
        simCard.makeCall(context, ussdCode)
    }
}
