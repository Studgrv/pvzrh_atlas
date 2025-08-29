package com.padi.newcompose.api.service

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Url
import java.util.concurrent.TimeUnit

object GeneralRetrofit {
    private const val BASE_URL = "https://example.com/"

    interface GeneralService {
        @GET()
        fun request(
            @Url url: String
        ): Deferred<ResponseBody>
    }

    val instance: GeneralService by lazy {
        // Create an interceptor to add headers
        val headerInterceptor = Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("referer", "https://wiki.biligame.com/")
                .addHeader("user-agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/139.0.0.0 Safari/537.36")
                .build()
            chain.proceed(request)
        }

        // Build the OkHttpClient with the interceptor
        val okHttpClient = OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(headerInterceptor) // Add the interceptor here
            .build()

        // Build Retrofit with the updated OkHttpClient
        Retrofit.Builder().baseUrl(BASE_URL).client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory()).build()
            .create(GeneralService::class.java)
    }
}
