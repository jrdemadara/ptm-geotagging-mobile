package com.jrdemadara.ptm_geotagging.server

import com.jrdemadara.ptm_geotagging.data.Municipality
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query
import retrofit2.http.QueryMap
import java.util.logging.Filter

interface ApiInterface {
    @Headers("Content-Type:application/json")
    @GET("municipality")
    fun getMunicipalities(): Call<List<Municipality>>

    @Headers("Content-Type:application/json")
    @POST("register")
    fun registerUser(@QueryMap filter: HashMap<String, String>): Call<ResponseBody>

    @Headers("Content-Type:application/json")
    @POST("login")
    fun loginUser(@QueryMap filter: HashMap<String, String>): Call<ResponseBody>

//    @Headers("Content-Type:application/json")
//    @POST("profile")
//    fun uploadProfile(@QueryMap filter: HashMap<String, Any>): Call<ResponseBody>

    @Multipart
    @POST("profile")
    fun uploadProfile(
        @Part("data") data: RequestBody,
        @Part personal: MultipartBody.Part,
        @Part family: MultipartBody.Part,
        @Part livelihood: MultipartBody.Part
    ): Call<ResponseBody>
}