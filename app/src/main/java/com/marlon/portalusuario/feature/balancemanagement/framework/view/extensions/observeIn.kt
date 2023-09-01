package com.marlon.portalusuario.feature.balancemanagement.framework.view.extensions

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

fun <T> Flow<T>.observeIn(
    lifecycleOwner: LifecycleOwner,
    action: (T) -> Unit
) {
    val lifecycleScope = lifecycleOwner.lifecycle.coroutineScope
    lifecycleScope.launch {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            collect { value ->
                action(value)
            }
        }
    }
}