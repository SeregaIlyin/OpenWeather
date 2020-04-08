package com.openweather.ui.main

import android.graphics.Bitmap
import com.openweather.ui.base.MvpView

interface MainMvpView: MvpView {
    fun setTemperature(temperature: String?)
    fun setWind(wind: String?)
    fun setPicture(img: Bitmap?)
    fun setPlace(place: String?)
    fun openLocationSettings()
    fun setPbVisibility(visibility: Int)
}