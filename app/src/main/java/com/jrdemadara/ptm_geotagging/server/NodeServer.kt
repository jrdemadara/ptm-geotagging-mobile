package com.jrdemadara.ptm_geotagging.server

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NodeServer {
    companion object {
        private const val BASE_URL: String = "https://geoapp.jrdemadara.dev/api/v1/uri/"

        fun getRetrofitInstance(token: String): Retrofit {
            // Create an interceptor to add the Bearer token to the request headers
            val interceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }.let { interceptor ->
                interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
                interceptor
            }

            val client = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer $token")
                        .build()
                    chain.proceed(request)
                }
                .addInterceptor(interceptor)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }
}