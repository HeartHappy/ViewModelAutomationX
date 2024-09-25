package com.hearthappy.vma_ktx.network

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainCoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel生成使用
 * @receiver ViewModel
 * @param io SuspendFunction0<HttpResponse>
 * @param onSucceed Function2<R, HttpResponse, Unit>
 * @param onFailure Function1<FailedBody, Unit>
 * @param onThrowable Function1<Throwable, Unit>
 * @param dispatcher CoroutineDispatcher
 */
inline fun <reified R : Any> ViewModel.requestScopeX(crossinline io: suspend () -> R, crossinline onSucceed: (R) -> Unit, crossinline onThrowable: (Throwable) -> Unit, crossinline onDataStore:suspend CoroutineScope.(R) -> Unit = {}, dispatcher: CoroutineDispatcher = Dispatchers.Main) {

    viewModelScope.launch(Dispatchers.IO) {
        try {
            val result = io()
            onDataStore(result)
            withMainCoroutine(dispatcher) { onSucceed(result) }
        } catch (e: Throwable) {
            e.printStackTrace()
            withMainCoroutine(dispatcher) { onThrowable(e) }
        }
    }
}

fun <R> ViewModel.requestScope(io: suspend () -> R, onSucceed: (R) -> Unit, onThrowable: (Throwable) -> Unit, onDataStore: suspend CoroutineScope.(R) -> Unit = {}, dispatcher: CoroutineDispatcher = Dispatchers.Main) {

    viewModelScope.launch(Dispatchers.IO) {
        try {
            val result = io()
            onDataStore(result)
            withMainCoroutine(dispatcher) { onSucceed(result) }
        } catch (e: Throwable) {
            e.printStackTrace()
            withMainCoroutine(dispatcher) { onThrowable(e) }
        }
    }
}

suspend fun withMainCoroutine(dispatcher: CoroutineDispatcher, block: () -> Unit) {
    when (dispatcher) {
        is MainCoroutineDispatcher -> withContext(Dispatchers.Main) { block() }
        else                       -> block()
    }
}







