package com.qualifio.internal.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal val qualifioCoroutineScope: CoroutineScope by lazy {
    CoroutineScope(Dispatchers.IO + SupervisorJob())
}

internal fun <A, T> toCallbackFunction(
    fn: suspend (A) -> T,
): (
    a: A,
    callbackSuccess: (T) -> Unit,
    callbackFailure: (Exception) -> Unit,
) -> Unit =
    { a, callbackSuccess, callbackFailure ->
        awaitSuspend(suspend { fn(a) }, callbackSuccess, callbackFailure)
    }

private fun <T> awaitSuspend(
    fn: (suspend () -> T),
    callbackSuccess: (T) -> Unit,
    callbackFailure: (Exception) -> Unit,
) {
    qualifioCoroutineScope.launch {
        try {
            val result = fn()

            withContext(Dispatchers.Main) {
                callbackSuccess(result)
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                callbackFailure(e)
            }
        }
    }
}
