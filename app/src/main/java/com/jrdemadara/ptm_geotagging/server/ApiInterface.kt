package com.jrdemadara.ptm_geotagging.server

import com.jrdemadara.ptm_geotagging.data.Municipality
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.QueryMap
import java.util.logging.Filter

interface ApiInterface {
    @Headers("Content-Type:application/json")
    @GET("workers")
    fun getMunicipalities(): Call<List<Municipality>>

    @Headers("Content-Type:application/json")
    @POST("register")
    fun registerUser(@QueryMap filter: HashMap<String, String>): Call<ResponseBody>
}