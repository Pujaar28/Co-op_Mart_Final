package com.pujaad.coopmart.api

import com.pujaad.coopmart.BuildConfig
import okhttp3.Headers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiFactory {
    private var API_BASE_URL_SERVER = "http://co-opmartman2.my.id/index.php/api/" // swagger
//    private var API_BASE_URL_SERVER = "http://10.113.10.157/coop-mart-api/index.php/api/" // swagger

    private lateinit var httpClient: OkHttpClient.Builder

    fun <S> createService(service: Class<S>, pref: Prefs): S {
        httpClient = OkHttpClient.Builder().addInterceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder()
                .headers(setHeaders(pref.token ?: ""))
                .method(original.method, original.body)
            chain.proceed(request.build())
        }
            .callTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)

        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            httpClient.addInterceptor(logging)
        }

        //comment this if want to connect to real service
        //httpClient.addInterceptor(MockClient(context))
//        val retrofit = Retrofit.Builder()
//            .baseUrl(baseUrl)
//            .addConverterFactory(GsonConverterFactory.create())
//            .client(httpClient.build())
//            .build()
//        return retrofit.create(service)
        return buildApi(service, API_BASE_URL_SERVER)
    }

    private fun <S> buildApi(service: Class<S>, url: String) : S = Retrofit.Builder()
        .baseUrl(url)
        .addConverterFactory(GsonConverterFactory.create())
        .client(httpClient.build())
        .build().create(service)

    private fun setHeaders(token: String = ""): Headers {
        val builder = Headers.Builder()
        if (token.isNotEmpty() && token.isNotBlank()) builder.add("Authorization", "Bearer $token")
        return builder.build()
    }

}