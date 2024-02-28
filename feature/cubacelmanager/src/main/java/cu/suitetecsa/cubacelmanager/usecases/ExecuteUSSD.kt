package cu.suitetecsa.cubacelmanager.usecases

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import cu.suitetecsa.sdk.android.model.SimCard
import cu.suitetecsa.sdk.android.utils.makeCall
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ExecuteUSSD @Inject constructor(@ApplicationContext private val context: Context) {
    @RequiresPermission(Manifest.permission.CALL_PHONE)
    operator fun invoke(simCard: SimCard, ussdCode: String) {
        simCard.makeCall(context, ussdCode)
    }
}
