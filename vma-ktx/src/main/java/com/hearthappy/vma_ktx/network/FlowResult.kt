package com.hearthappy.vma_ktx.network

/**
 * Created Date: 2025/5/13
 * @author ChenRui
 * ClassDescription：StateFlow响应结果
 */
sealed class FlowResult<out T> {
    data object Loading : FlowResult<Nothing>()
    data class Succeed<T>(val body: T, val order: Int = InSitu) : FlowResult<T>()
//    data class Failed(val failedBody: FailedBody, val order: Int = InSitu) : FlowResult<Nothing>()
    data class Throwable(val throwable: kotlin.Throwable, val order: Int = InSitu) : FlowResult<Nothing>()
    data object Default : FlowResult<Nothing>()
}