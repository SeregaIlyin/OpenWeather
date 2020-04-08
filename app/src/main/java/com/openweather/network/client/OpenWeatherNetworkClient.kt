package com.openweather.network.client

import android.os.Build
import com.openweather.network.TLSSocketFactory
import com.openweather.utils.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

object OpenWeatherNetworkClient {
    var retrofit: Retrofit? = null
        get() {
            if (field == null) {
                field = Retrofit.Builder()
                    .baseUrl(Constants.OPEN_WEARTHER_API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) okHttpClient else kitKatOkHttpClient)
                    .build()
            }
            return field
        }
        private set

    private val okHttpClient: OkHttpClient
        get() {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            return OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .readTimeout(130, TimeUnit.SECONDS)
                .connectTimeout(130, TimeUnit.SECONDS)
                .build()
        }

    // Create an ssl socket factory with our all-trusting manager
    @Suppress("DEPRECATION")
    private val kitKatOkHttpClient: OkHttpClient
        get() = try {
            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory = TLSSocketFactory()
            val builder = OkHttpClient.Builder()
            builder.sslSocketFactory(sslSocketFactory)
            builder.hostnameVerifier { _, _ -> true }
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            builder
                .addInterceptor(interceptor)
                .readTimeout(130, TimeUnit.SECONDS)
                .connectTimeout(130, TimeUnit.SECONDS)
                .build()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
}