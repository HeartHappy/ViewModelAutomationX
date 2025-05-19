package com.hearthappy.vma_ktx.network

/**5
 * A generic class that holds a value with its loading status.
 * @param <T> LiveData
 */
sealed class Result<out T : Any> {

    data class Succeed<out T : Any>(val body: T, val order: Int = InSitu) : Result<T>()
//    data class Failed(val failedBody: FailedBody, val order: Int = InSitu) : Result<Nothing>()
    data class Throwable(val throwable: kotlin.Throwable, val order: Int = InSitu) : Result<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Succeed<*> -> "Success[body=$body]"
//            is Failed     -> "Failed[failedBody=$failedBody]"
            is Throwable  -> "Throwable[throwable=$throwable]"
        }
    }
}






