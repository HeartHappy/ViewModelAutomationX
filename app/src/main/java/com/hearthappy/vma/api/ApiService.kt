package com.hearthappy.vma.api

import com.hearthappy.annotations.BindLiveData
import com.hearthappy.annotations.BindStateFlow
import com.hearthappy.annotations.ViewModelAutomation
import com.hearthappy.ksp.model.request.LoginBody
import com.hearthappy.ksp.model.response.ResImages
import com.hearthappy.ksp.model.response.ResLogin
import com.hearthappy.vma.model.response.BaseData
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

@ViewModelAutomation("MainTestViewModel", enableLog = false)
interface ApiService {


    @BindStateFlow
    @POST("login")
    suspend fun login(@Body loginBody: LoginBody): ResLogin

    @BindStateFlow("getImages", "sfImages")
    @GET("getImages")
    suspend fun getImage(@Query("page") page: Int, @Query("size") size: Int): ResImages

    @BindLiveData("getToken")
    @POST("room/getEasemob/token")
    @FormUrlEncoded
    suspend fun getToken(@Field("deviceID") deviceID: String, @Field("phone_model") phone_model: String): BaseData<ResLogin>


}