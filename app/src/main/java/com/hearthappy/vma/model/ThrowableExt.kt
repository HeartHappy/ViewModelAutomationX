package com.hearthappy.vma.model

import android.util.Log
import com.google.gson.JsonSyntaxException
import com.hearthappy.vma_ktx.network.FlowResult
import com.hearthappy.vma_ktx.network.Result
import okhttp3.internal.http2.ConnectionShutdownException
import java.net.SocketTimeoutException

/**
 * Created Date: 2025/5/20
 * @author ChenRui
 * ClassDescription：异常处理并显示Toast
 */
fun FlowResult.Throwable.toast() {
    Log.d("Throwable", "toast: ${throwableErrorMessage(throwable)}")
//    ToastUtils.showLong(throwableErrorMessage(throwable))
}

fun Result.Throwable.toast() {
    Log.d("Throwable", "toast: ${throwableErrorMessage(throwable)}")
//    ToastUtils.showLong(throwableErrorMessage(throwable))
}

fun throwableErrorMessage(throwable: Throwable) = when (throwable) {
    is SocketTimeoutException -> "连接超时"
    is ConnectionShutdownException -> "HTTP连接被关闭"
    is JsonSyntaxException -> "JSON解析失败"
    else -> "其他异常:${throwable.javaClass.name}-${throwable.message}"
}