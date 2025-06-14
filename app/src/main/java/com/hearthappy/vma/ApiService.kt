package com.hearthappy.vma

import com.hearthappy.annotations.BindLiveData
import com.hearthappy.annotations.BindStateFlow
import com.hearthappy.annotations.ViewModelAutomation
import com.hearthappy.vma.model.request.LoginBody
import com.hearthappy.vma.model.response.BaseModel
import com.hearthappy.vma.model.response.ResImages
import com.hearthappy.vma.model.response.ResLogin
import com.hearthappy.vma.model.response.ResSentences
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

@ViewModelAutomation("MainViewModel")
interface ApiService {


    @BindStateFlow
    @POST("login")
    suspend fun login(@Body loginBody: LoginBody): BaseModel<ResLogin>

    @BindStateFlow("getImages", "sfImages")
    @GET("getImages")
    suspend fun getImage(@Query("page") page: Int, @Query("size") size: Int):BaseModel<ResImages>


    @BindLiveData
    @GET("sentences")
    suspend fun getSentences(): BaseModel<ResSentences>

}