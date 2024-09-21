package com.hearthappy.vma_ktx.network

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * ViewModel生成使用
 * @receiver ViewModel
 * @param io SuspendFunction0<HttpResponse>
 * @param onSucceed Function2<R, HttpResponse, Unit>
 * @param onFailure Function1<FailedBody, Unit>
 * @param onThrowable Function1<Throwable, Unit>
 * @param dispatcher CoroutineDispatcher
 */
inline fun <reified R : Any> ViewModel.requestScope(crossinline io: suspend () -> R, crossinline onSucceed: (R) -> Unit, crossinline onThrowable: (Throwable) -> Unit, dispatcher: CoroutineDispatcher = Dispatchers.Main) {
    viewModelScope.launch(Dispatchers.IO) {
        requestHandler(io, onSucceed, onThrowable, dispatcher)
    }
}

suspend inline fun <reified R> requestHandler(crossinline io: suspend () -> R, crossinline onSucceed: (R) -> Unit, crossinline onThrowable: (Throwable) -> Unit, dispatcher: CoroutineDispatcher) {
    try {
        responseHandler(io(), dispatcher, onSucceed)
    } catch (e: Throwable) {
        withMainCoroutine(dispatcher) {
            e.printStackTrace()
            onThrowable(e)
        }
    }
}







