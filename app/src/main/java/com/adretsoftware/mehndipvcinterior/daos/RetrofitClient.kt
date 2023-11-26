package com.adretsoftware.mehndipvcinterior.daos

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitClient {
    private var retrofitApiHolder: RetrofitApiHolder

    init {


//        val interceptor = HttpLoggingInterceptor()
//        interceptor.level = HttpLoggingInterceptor.Level.BODY
//
//        val client = OkHttpClient.Builder()
//            .callTimeout(60, TimeUnit.SECONDS)
//            .connectTimeout(60, TimeUnit.SECONDS)
//            .readTimeout(60, TimeUnit.SECONDS)
//            .writeTimeout(60, TimeUnit.SECONDS)
//            .addInterceptor(object : Interceptor {
//                override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
//                    val builder1 = chain.request().newBuilder()
//
//                    val response = chain.proceed(builder1.build())
//                    return response
//                }
//            }).addInterceptor(interceptor).build()


        val gson = GsonBuilder()
            .setLenient()
            .create()
//        val retrofit= Retrofit.Builder().baseUrl(Constants.apiUrl)
//            .addConverterFactory(GsonConverterFactory.create(gson)).build()

        val httpClient = OkHttpClient.Builder()
        httpClient.cache(null)

        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.apiUrl)
            .client(httpClient.build())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        retrofitApiHolder = retrofit.create(RetrofitApiHolder::class.java)
    }

    fun getApiHolder(): RetrofitApiHolder {
        return retrofitApiHolder
    }

}