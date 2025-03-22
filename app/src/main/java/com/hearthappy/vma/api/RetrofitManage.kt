package com.hearthappy.vma.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitManage {
    private val retrofit: Retrofit by lazy { initRetrofit() }
    private const val CONNECT_TIMEOUT = 60L
    private const val READ_TIMEOUT = 100L
    private const val WRITE_TIMEOUT = 60L

        private const val BASE_URL = "https://api.apiopen.top/api/"

    private fun initRetrofit(): Retrofit {
        val okHttpClientBuilder = OkHttpClient().newBuilder().apply {
            connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            addInterceptor(ResponseInterceptor())
//            addInterceptor(DataEncryptInterceptor())
//            addInterceptor(ProxyInterceptor())
        }
        return Retrofit.Builder().apply {
            baseUrl(BASE_URL)
            client(okHttpClientBuilder.build())
            addConverterFactory(GsonConverterFactory.create())
        }.build()
    }

//    @VMAApi
//    fun apiService2() = retrofit.create(ApiService::class.java)

    val apiService = retrofit.create(ApiService::class.java)


}