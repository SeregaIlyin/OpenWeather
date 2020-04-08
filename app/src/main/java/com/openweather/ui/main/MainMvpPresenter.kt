package com.openweather.ui.main

import com.openweather.ui.base.MvpPresenter

interface MainMvpPresenter<V: MainMvpView?>: MvpPresenter<V?> {
    fun setUp()
    fun getWeather()
    fun startCheckWeather()
    fun stopCheckWeather()
}