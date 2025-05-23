package com.hearthappy.vma_ktx.network

/**
 * @author ChenRui
 * ClassDescription： LiveData响应结果
 */
sealed class Result<out T : Any> {

    data class Succeed<out T : Any>(val body: T, val order: Int = InSitu) : Result<T>()
//    data class Failed(val failedBody: FailedBody, val order: Int = InSitu) : Result<Nothing>()
    data class Throwable(val throwable: kotlin.Throwable, val order: Int = InSitu) : Result<Nothing>()

}






