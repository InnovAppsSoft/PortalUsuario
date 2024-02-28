package cu.suitetecsa.core.utils

import androidx.core.text.isDigitsOnly
import com.journeyapps.barcodescanner.ScanOptions

private const val MaxLengthTopUpCode = 16
private const val MinLengthTopUpCode = 12

object SuitEtecsaUtils {
    val qrOptions = ScanOptions().apply {
        setDesiredBarcodeFormats(ScanOptions.QR_CODE)
        setCameraId(0)
        setBeepEnabled(false)
        setOrientationLocked(false)
    }

    val String.isValidTopUpCode: Boolean
        get() = this.length in listOf(MinLengthTopUpCode, MaxLengthTopUpCode) && this.isDigitsOnly()
}
