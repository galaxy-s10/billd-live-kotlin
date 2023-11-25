package com.example.billd_live_kotlin

import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit


val httpClient = OkHttpClient.Builder()
//    .connectTimeout(1,TimeUnit.MILLISECONDS)
    .connectTimeout(10,TimeUnit.SECONDS)
    .build()

val baseUrl = "https://live-api.hsslive.cn"
val retrofit = Retrofit.Builder()
    .baseUrl(baseUrl)
    .client(httpClient)
    .addConverterFactory(GsonConverterFactory.create())  // 如果你希望使用 Gson 进行 JSON 解析，需要添加该转换器
    .build()


interface LiveApiService {
    @GET("/live/list")
    suspend fun getLiveList(@Query("orderName") orderName: String): Call<Any>
}


interface UserApiService {
    // suspend必须要加，否则错误的时候捕获不到
    @POST("/user/login")
    fun login(@Body body: RequestBody):  Call<Any>

    @GET("/user/get_user_info")
    suspend fun getUserInfo(): Call<Any>
}

interface SrsApiService {
    @GET("/srs/rtcV1Publish")
    suspend fun getRtcV1Publish(): Call<Any>
}


class BilldApi {
    val live = retrofit.create(LiveApiService::class.java)
    val user = retrofit.create(UserApiService::class.java)
    val srs = retrofit.create(SrsApiService::class.java)
}
