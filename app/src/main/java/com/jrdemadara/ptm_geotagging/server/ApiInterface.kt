package com.jrdemadara.ptm_geotagging.server

import com.jrdemadara.ptm_geotagging.data.Municipality
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
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

    @Headers("Content-Type:application/json")
    @POST("profile")
    fun uploadProfile(@QueryMap filter: HashMap<String, Any>): Call<ResponseBody>
}