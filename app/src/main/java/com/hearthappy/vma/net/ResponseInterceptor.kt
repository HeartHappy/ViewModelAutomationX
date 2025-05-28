package com.hearthappy.vma.net

import android.util.Log
import com.google.gson.Gson
import com.hearthappy.vma.model.response.BaseModel
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

class ResponseInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        val response = chain.proceed(request)
        when (response.code) {
            200 -> {
                response.body?.use {
                    val jsonStr = it.string()
                    val baseModel = Gson().fromJson(jsonStr, BaseModel::class.java)
                    if (baseModel.code != 200) {
                        throw BusinessException(baseModel.message.plus("\n错误码:${baseModel.code}"))
                    } // 创建一个新的响应体，因为原响应体已经被消费
                    return response.newBuilder().body(jsonStr.toResponseBody(it.contentType())).build()
                }
            }
            in 500..599 -> {
                Log.e(TAG, "intercept: HTTP 服务器异常:${response.message} ,code: ${response.code},url:${request.url.toUrl()}")
            }

            else -> {
                Log.e(TAG, "intercept: HTTP 网络异常信息:${response.message} ,code: ${response.code},url:${request.url.toUrl()}")
            }
        }
        return response
    }

    companion object {
        private const val TAG = "ResponseInterceptor"
    }
}