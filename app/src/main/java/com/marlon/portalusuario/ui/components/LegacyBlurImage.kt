package com.marlon.portalusuario.ui.components

import android.graphics.Bitmap
import android.renderscript.Allocation
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext

@Composable
fun LegacyBlurImage(
    bitmap: Bitmap,
    blurRadio: Float,
    modifier: Modifier = Modifier
) {
    val renderScript = RenderScript.create(LocalContext.current)
    val bitmapAlloc = Allocation.createFromBitmap(renderScript, bitmap)
    ScriptIntrinsicBlur.create(renderScript, bitmapAlloc.element).apply {
        setRadius(blurRadio)
        setInput(bitmapAlloc)
        forEach(bitmapAlloc)
    }
    bitmapAlloc.copyTo(bitmap)
    renderScript.destroy()

    BlurImage(bitmap, modifier)
}

@Composable
fun BlurImage(bitmap: Bitmap, modifier: Modifier = Modifier) {
    Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
    )
}
