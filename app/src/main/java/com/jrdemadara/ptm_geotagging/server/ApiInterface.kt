package com.jrdemadara.ptm_geotagging.server

import com.jrdemadara.ptm_geotagging.data.Assistance
import com.jrdemadara.ptm_geotagging.data.AssistanceRequest
import com.jrdemadara.ptm_geotagging.data.Barangay
import com.jrdemadara.ptm_geotagging.data.Members
import com.jrdemadara.ptm_geotagging.data.Municipality
import com.jrdemadara.ptm_geotagging.data.ProfileResponse
import com.jrdemadara.ptm_geotagging.data.SearchMembers
import com.jrdemadara.ptm_geotagging.features.assistance_list.AssistanceList
import com.jrdemadara.ptm_geotagging.features.profiles.ProfilesActivity
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface ApiInterface {
    @Headers("Content-Type:application/json")
    @GET("municipality")
    fun getMunicipalities(): Call<List<Municipality>>

    @Headers("Content-Type:application/json")
    @GET("profiles")
    fun getProfileCount(): Call<ProfilesActivity.ProfileCountResponse>

    @Headers("Content-Type:application/json")
    @GET("barangay")
    fun getBarangay(@QueryMap filter: HashMap<String, String>): Call<List<Barangay>>

    @Headers("Content-Type:application/json")
    @GET("initialize-member")
    fun getMembers(@QueryMap filter: HashMap<String, String>): Call<List<Members>>


    @Headers("Content-Type:application/json")
    @GET("initialize-assistance")
    fun getAssistanceType(@QueryMap filter: HashMap<String, String>): Call<List<Assistance>>


    @Headers("Content-Type:application/json")
    @POST("register")
    fun registerUser(@QueryMap filter: HashMap<String, String>): Call<ResponseBody>

    @Headers("Content-Type:application/json")
    @POST("login")
    fun loginUser(@QueryMap filter: HashMap<String, String>): Call<ResponseBody>

    @Headers("Content-Type:application/json")
    @GET("search-member")
    fun searchMember(@Query("keyword") keyword: String): Call<List<SearchMembers>>

    @Multipart
    @POST("profile")
    fun uploadProfile(
        @Part("data") data: RequestBody,
        @Part personal: MultipartBody.Part,
        @Part family: MultipartBody.Part,
        @Part livelihood: MultipartBody.Part
    ): Call<ResponseBody>

    @Headers("Content-Type:application/json")
    @GET("validate-profile")
    fun validateProfile(
        @Query("qrcode") qrcode: String,
        @Query("assistance") assistance: String
    ): Call<ResponseBody>

    @Headers("Content-Type:application/json")
    @POST("release-assistance")
    fun releaseAssistance(
        @Body assistanceRequest: AssistanceRequest
    ): Call<ResponseBody>

    @Headers("Content-Type:application/json")
    @GET("validate-profile-personal")
    fun validateProfilePersonal(
        @Query("qrcode") qrcode: String,
    ): Call<ProfileResponse>

    @Headers("Content-Type:application/json")
    @GET("fetch-assistance-by-date")
    fun getAssistanceByDate(@QueryMap filter: HashMap<String, String>): Call<List<AssistanceList>>

}