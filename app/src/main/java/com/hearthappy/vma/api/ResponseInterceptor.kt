package com.hearthappy.vma.api

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class ResponseInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        val response: Response = chain.proceed(request)
//        Log.d(TAG, "intercept: code:${response.code},result: ${response.body}")
        when (response.code) {
            200         -> {
                Log.d(TAG, "intercept: 成功")
            }

            in 500..599 -> {
                Log.e(TAG, "intercept: HTTP 服务器异常:${response.message} ,code: ${response.code},url:${request.url.toUrl()}")
            }

            else        -> {
                Log.e(TAG, "intercept: HTTP 网络异常信息:${response.message} ,code: ${response.code},url:${request.url.toUrl()}")
            }
        }
        return response
    }

    companion object {
        private const val TAG = "ResponseInterceptor"
    }
}