package com.openweather.network.api

import com.openweather.model.openweather.CurrentWeatherResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface OpenWeatherApi {

    @Headers("Content-Type: application/json", "Accept: application/json")
    @GET("/data/2.5/weather")
    fun getCurrentWeather(
        @Query("lat") lat: Float?,
        @Query("lon") lon: Float?,
        @Query("appid") appId: String?,
        @Query("units") units: String?
    ): Observable<CurrentWeatherResponse>?
    //): Observable<CurrentWeatherResponse>?
}