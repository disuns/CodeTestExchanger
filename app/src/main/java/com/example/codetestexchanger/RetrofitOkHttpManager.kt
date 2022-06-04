package com.example.codetestexchanger

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

object RetrofitOkHttpManager {
    private var okHttpClient:OkHttpClient

    private val retrofitBuilder : Retrofit.Builder = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl("https://api.apilayer.com/")

    val restService : RestService
        get() = retrofitBuilder.build().create(RestService::class.java)

    init {
        okHttpClient = OkHttpClient.Builder().addInterceptor(Interceptor{ chain: Interceptor.Chain ->
            val request = chain.request()
            val newRequest : Request = request.newBuilder().addHeader("Accept","application/json").build()
            chain.proceed(newRequest)
        }).addInterceptor(RetryInterceptor()).connectTimeout(20L, TimeUnit.SECONDS).readTimeout(15L, TimeUnit.SECONDS).build()
        retrofitBuilder.client(okHttpClient)
    }

    private class RetryInterceptor(): Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            val request: Request = chain.request()
            var response: Response = chain.proceed(request)
            var tryCount = 0
            val maxLimit = 2
            while (!response.isSuccessful && tryCount < maxLimit) {
                tryCount++
                response = chain.proceed(request)
            }
            return response
        }
    }
}