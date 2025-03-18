package com.marlon.portalusuario.ui.components

import kotlinx.coroutines.delay

suspend fun <T>ListIterator<T>.doWhenHasNextOrPrevious(
    delayMills: Long = 3000,
    doWork: suspend (T) -> Unit
) {
    while (hasNext() || hasPrevious()) {
        while (hasNext()) {
            delay(delayMills)
            doWork(next())
        }
        while (hasPrevious()) {
            delay(delayMills)
            doWork(previous())
        }
    }
}