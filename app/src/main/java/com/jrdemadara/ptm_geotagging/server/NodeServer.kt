package com.jrdemadara.ptm_geotagging.server

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class NodeServer {
    companion object {
        //private const val BASE_URL: String = "https://geoapp.ptmkapamilya.org/api/v1/uri/"
        private const val BASE_URL: String = "http://192.168.113.8:8000/api/v1/uri/"

        fun getRetrofitInstance(token: String): Retrofit {
            // Create a logging interceptor to log the body of requests and responses
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            // Create the OkHttpClient and add interceptors
            val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer $token")
                        .build()
                    chain.proceed(request)
                }
                .addInterceptor(loggingInterceptor)
                .build()

            // Create and return the Retrofit instance
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }
}