package com.hearthappy.vma_ktx.network

sealed class FlowResult<out T> {
    data object Loading : FlowResult<Nothing>()
    data class Succeed<T>(val body: T, val order: Int = InSitu) : FlowResult<T>()
//    data class Failed(val failedBody: FailedBody, val order: Int = InSitu) : FlowResult<Nothing>()
    data class Throwable(val throwable: kotlin.Throwable, val order: Int = InSitu) : FlowResult<Nothing>()
    data object Default : FlowResult<Nothing>()
}