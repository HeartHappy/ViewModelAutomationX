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
 * Created Date: 2024/1/4
 * @author ChenRui
 * ClassDescription： ViewModel生成后调用
 */
//inline fun <reified R : Any> ViewModel.requestScopeX(crossinline io: suspend () -> R, crossinline onSucceed: (R) -> Unit, crossinline onThrowable: (Throwable) -> Unit, crossinline onDataStore: suspend CoroutineScope.(R) -> Unit = {}, dispatcher: CoroutineDispatcher = Dispatchers.Main) {
//
//    viewModelScope.launch(Dispatchers.IO) {
//        try {
//            val result = io()
//            onDataStore(result)
//            withMainCoroutine(dispatcher) { onSucceed(result) }
//        } catch (e: Throwable) {
//            e.printStackTrace()
//            withMainCoroutine(dispatcher) { onThrowable(e) }
//        }
//    }
//}

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
        else -> block()
    }
}







