package com.hearthappy.vma.model

import com.google.gson.JsonSyntaxException
import com.hearthappy.vma.model.response.BaseModel
import com.hearthappy.vma_ktx.network.FlowResult
import com.hearthappy.vma_ktx.network.Result
import com.hjq.toast.ToastUtils
import okhttp3.internal.http2.ConnectionShutdownException
import java.net.SocketTimeoutException

/**
 * Created Date: 2025/5/20
 * @author ChenRui
 * ClassDescription：异常处理并显示Toast
 */
fun FlowResult.Throwable.toast() {
    ToastUtils.show(throwableErrorMessage(throwable))
}

fun Result.Throwable.toast() {
    ToastUtils.show(throwableErrorMessage(throwable))
}


private fun throwableErrorMessage(throwable: Throwable) = when (throwable) {
    is SocketTimeoutException -> "连接超时"
    is ConnectionShutdownException -> "HTTP连接被关闭"
    is JsonSyntaxException -> "JSON解析失败"
    else -> throwable.message
}

/**
 * 成功返回，能够解析到data数据,返回data作用域
 * @receiver BaseModel<T>
 * @param block [@kotlin.ExtensionFunctionType] Function1<T, Unit>
 */
fun <T> Result.Succeed<BaseModel<T>>.process(block: T.() -> Unit) = body.processHandler(block)
fun <T> FlowResult.Succeed<BaseModel<T>>.process(block: T.() -> Unit) = body.processHandler(block)

private fun <T> BaseModel<T>.processHandler(block: T.() -> Unit) {
    result?.let { block(it) }
}