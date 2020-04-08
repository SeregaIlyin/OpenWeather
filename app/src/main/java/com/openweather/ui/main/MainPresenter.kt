package com.openweather.ui.main

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import android.text.TextUtils
import android.view.View
import com.bumptech.glide.Glide
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.*
import com.openweather.OpenWeatherApplication
import com.openweather.model.openweather.CurrentWeatherResponse
import com.openweather.model.realm.CurrentWeatherRealmObject
import com.openweather.model.realm.WeatherRealm
import com.openweather.network.CallbackWrapper
import com.openweather.network.api.OpenWeatherApi
import com.openweather.network.client.OpenWeatherNetworkClient
import com.openweather.ui.base.BasePresenter
import com.openweather.utils.CommonUtils
import com.openweather.utils.Constants
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class MainPresenter<V : MainMvpView?> @Inject internal constructor(compositeDisposable: CompositeDisposable?) : BasePresenter<V?>(compositeDisposable), MainMvpPresenter<V?> {
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var timerDisposable: Disposable? = null

    override fun setUp() {

    }

    private fun isGooglePlayServicesAvailable(): Boolean {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val status = googleApiAvailability.isGooglePlayServicesAvailable(OpenWeatherApplication.getContext())
        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
                mvpView?.showMessage(OpenWeatherApplication.getContext()?.getString(com.openweather.R.string.activity_main_google_service_not_available)
                    ?.format(status)!!)
            }
            return false
        }
        return true
    }

    override fun startCheckWeather() {
        stopCheckWeather()

        var initDelay : Long = 0
        val realm = Realm.getDefaultInstance()
        try {
            val currentWeatherRealmObject =
                realm.where(CurrentWeatherRealmObject::class.java).findAll().last()
            val now = System.currentTimeMillis()
            val delta = (now - currentWeatherRealmObject?.uid!!) / 1000
            if(delta<600){
                initDelay = 600 - delta

                downloadWeaterIcon(currentWeatherRealmObject.weather[0]?.icon!!)
                mvpView?.setTemperature(OpenWeatherApplication.getContext()?.getString(com.openweather.R.string.activity_main_current_temperature)
                    ?.format(currentWeatherRealmObject.main?.temp.toString()))
                mvpView?.setWind(OpenWeatherApplication.getContext()?.getString(com.openweather.R.string.activity_main_current_wind)
                    ?.format(currentWeatherRealmObject.wind?.speed.toString(), CommonUtils.degToText(currentWeatherRealmObject.wind?.deg!!)))
                mvpView?.setPlace(OpenWeatherApplication.getContext()?.getString(com.openweather.R.string.activity_main_current_place)
                    ?.format(if (TextUtils.isEmpty(currentWeatherRealmObject.name)) "" else currentWeatherRealmObject.name,
                        if (TextUtils.isEmpty(currentWeatherRealmObject.sys?.country)) "" else currentWeatherRealmObject.sys?.country,
                        currentWeatherRealmObject.coord?.lat, currentWeatherRealmObject.coord?.lon))
            }
        }catch (e: Exception){
            e.printStackTrace()
        }finally {
            realm.close()
        }

        timerDisposable =
            Observable
                .interval(initDelay, 600, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { getWeather() }

        getCompositeDisposable()!!.add(timerDisposable!!)
    }

    override fun stopCheckWeather() {
        if (timerDisposable != null) {
            timerDisposable!!.dispose()
        }
    }

    override fun getWeather(){
        if(!isGooglePlayServicesAvailable()){
            return
        }

        if(!mvpView?.hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION)!!){
            mvpView?.requestPermissionsSafely(
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                Constants.GET_ACCESS_COARSE_LOCATION_REQUEST)
        }else if(!mvpView?.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)!!){
            mvpView?.requestPermissionsSafely(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                Constants.GET_ACCESS_FINE_LOCATION_REQUEST)
        }else{
            if (isLocationEnabled()) {
                mvpView?.setPbVisibility(View.VISIBLE)
                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(OpenWeatherApplication.getContext()!!)
                mFusedLocationClient.lastLocation.addOnCompleteListener { task ->
                    requestNewLocationData()
                    /*val location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        updateWeater(location.latitude.toFloat(),location.longitude.toFloat())
                    }*/
                }
            } else {
                mvpView?.showMessage(com.openweather.R.string.activity_main_turn_location_on)
                mvpView?.openLocationSettings()
            }
        }
    }

    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(OpenWeatherApplication.getContext()!!)
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper())
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location = locationResult.lastLocation
            updateWeather(mLastLocation.latitude.toFloat(),mLastLocation.longitude.toFloat())
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            OpenWeatherApplication.getContext()?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun updateWeather(lat: Float, lon: Float){
        OpenWeatherNetworkClient.retrofit?.create(
            OpenWeatherApi::class.java
        )?.getCurrentWeather(lat, lon, Constants.API_KEY, Constants.UNITS_METRIC)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribeWith(
                object: CallbackWrapper<CurrentWeatherResponse>(this@MainPresenter) {
                    override fun onSuccess(t: CurrentWeatherResponse) {
                        downloadWeaterIcon(t.weather[0].icon)
                        mvpView?.setTemperature(OpenWeatherApplication.getContext()?.getString(com.openweather.R.string.activity_main_current_temperature)?.format(t.main.temp.toString()))
                        mvpView?.setWind(OpenWeatherApplication.getContext()?.getString(com.openweather.R.string.activity_main_current_wind)?.format(t.wind.speed.toString(), CommonUtils.degToText(t.wind.deg)))
                        mvpView?.setPlace(OpenWeatherApplication.getContext()?.getString(com.openweather.R.string.activity_main_current_place)
                            ?.format(if (TextUtils.isEmpty(t.name)) "" else t.name,
                                     if (TextUtils.isEmpty(t.sys.country)) "" else t.sys.country,
                                     lat, lon))
                        saveToRealm(t)
                    }

                    override fun onError(e: Throwable) {
                        mvpView?.setPbVisibility(View.GONE)
                        super.onError(e)
                    }
                }
            )?.let {
                getCompositeDisposable()?.add(
                    it
                )
            }
    }

    private fun saveToRealm(currentWeatherResponse: CurrentWeatherResponse){
        val realm: Realm = Realm.getDefaultInstance()
        try {
            realm.beginTransaction()
            val currentWeatherRealmObject = realm.createObject(CurrentWeatherRealmObject::class.java, System.currentTimeMillis())

            currentWeatherRealmObject.coord?.lat = currentWeatherResponse.coord.lat
            currentWeatherRealmObject.coord?.lon = currentWeatherResponse.coord.lon

            for (weather in currentWeatherResponse.weather) {
                val weatherRealm = WeatherRealm()
                weatherRealm.id = weather.id
                weatherRealm.main = weather.main
                weatherRealm.description = weather.description
                weatherRealm.icon = weather.icon
                currentWeatherRealmObject.weather.add(weatherRealm)
            }

            currentWeatherRealmObject.base = currentWeatherResponse.base

            currentWeatherRealmObject.main?.temp = currentWeatherResponse.main.temp
            currentWeatherRealmObject.main?.tempMax = currentWeatherResponse.main.tempMax
            currentWeatherRealmObject.main?.tempMin = currentWeatherResponse.main.tempMin
            currentWeatherRealmObject.main?.feelsLike = currentWeatherResponse.main.feelsLike
            currentWeatherRealmObject.main?.humidity = currentWeatherResponse.main.humidity
            currentWeatherRealmObject.main?.pressure = currentWeatherResponse.main.pressure

            currentWeatherRealmObject.visibility = currentWeatherResponse.visibility

            currentWeatherRealmObject.wind?.speed = currentWeatherResponse.wind.speed
            currentWeatherRealmObject.wind?.deg = currentWeatherResponse.wind.deg

            currentWeatherRealmObject.clouds?.all = currentWeatherResponse.clouds.all
            currentWeatherRealmObject.dt = currentWeatherResponse.dt
            currentWeatherRealmObject.sys?.id = currentWeatherResponse.sys.id
            currentWeatherRealmObject.sys?.country = currentWeatherResponse.sys.country
            currentWeatherRealmObject.sys?.sunrise = currentWeatherResponse.sys.sunrise
            currentWeatherRealmObject.sys?.sunset = currentWeatherResponse.sys.sunset
            currentWeatherRealmObject.sys?.type = currentWeatherResponse.sys.type
            currentWeatherRealmObject.timezone = currentWeatherResponse.timezone
            currentWeatherRealmObject.id = currentWeatherResponse.id
            currentWeatherRealmObject.name = currentWeatherResponse.name
            currentWeatherRealmObject.cod = currentWeatherResponse.cod

            realm.commitTransaction()
        } catch (e: Exception) {
            e.printStackTrace()
            realm.cancelTransaction()
        } finally {
            realm.close()
        }
    }

    private fun downloadWeaterIcon(imgName: String){
        object : Thread() {
            override fun run() {
                synchronized(this) {
                    val url = Constants.OPEN_WEARTHER_IMG_URL.format(imgName)
                    val bmp: Bitmap = Glide.with(OpenWeatherApplication.getContext())
                        .asBitmap()
                        .load(url)
                        .submit(64,64)
                        .get()

                    mvpView?.setPbVisibility(View.GONE)
                    mvpView?.setPicture(bmp)
                }
            }
        }.start()
    }

}