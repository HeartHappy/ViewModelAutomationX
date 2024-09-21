package com.hearthappy.vma_ktx.network

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainCoroutineDispatcher
import kotlinx.coroutines.withContext

val succeedCode = 200..299


suspend inline fun <reified R> responseHandler(response: R, dispatcher: CoroutineDispatcher, crossinline onSucceed: (R) -> Unit) {
    withMainCoroutine(dispatcher) {
        onSucceed(response)
    }
}

suspend fun withMainCoroutine(dispatcher: CoroutineDispatcher, block: () -> Unit) {
    when (dispatcher) {
        is MainCoroutineDispatcher -> {
            withContext(Dispatchers.Main) { block() }
        }

        else                       -> block()
    }
}

data class FailedBody(val statusCode: Int, val text: String?)

data class ErrorMessage(val error: String)


fun FlowResult.Failed.asFailedMessage(): String? {
    return failedBody.getFailedMessage()
}

fun Result.Failed.asFailedMessage(): String? {
    return failedBody.getFailedMessage()
}

fun FlowResult.Throwable.asThrowableMessage(): String? {
    return this.throwable.message
}

fun Result.Throwable.asThrowableMessage(): String? {
    return this.throwable.message
}


private fun FailedBody?.getFailedMessage(): String? {
    return this?.text

//    return try { //如果响应的错误消息中包含error时，获取error消息，否则原文本返回
//        this?.text?.let { GsonBuilder().create().fromJson(this.text, ErrorMessage::class.java).error }
//    } catch (e: Throwable) {
//        this?.text
//    }
}

const val InSitu = 0