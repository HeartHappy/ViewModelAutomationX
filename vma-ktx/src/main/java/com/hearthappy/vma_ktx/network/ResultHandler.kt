package com.hearthappy.vma_ktx.network


data class FailedBody(val statusCode: Int, val text: String?)

data class ErrorMessage(val error: String)


//fun FlowResult.Failed.asFailedMessage(): String? {
//    return failedBody.getFailedMessage()
//}
//
//fun Result.Failed.asFailedMessage(): String? {
//    return failedBody.getFailedMessage()
//}

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