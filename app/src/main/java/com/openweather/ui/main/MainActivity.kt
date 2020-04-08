package com.openweather.ui.main

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.opengl.Visibility
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.openweather.R
import com.openweather.ui.base.BaseActivity
import com.openweather.utils.Constants
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject


class MainActivity : BaseActivity(), MainMvpView {

    @Inject
    lateinit var mPresenter: MainPresenter<MainMvpView?>

    @BindView(R.id.txt_temperature)
    lateinit var txtTemperature: TextView

    @BindView(R.id.txt_wind)
    lateinit var txtWind: TextView

    @BindView(R.id.txt_place)
    lateinit var txtPlace: TextView

    @BindView(R.id.img_temperture)
    lateinit var imgTemperature: ImageView

    @BindView(R.id.pb)
    lateinit var pb: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        activityComponent?.inject(this)
        mPresenter.onAttach(this)
        unBinder = ButterKnife.bind(this)
        setSupportActionBar(toolbar)
        mPresenter.setUp()
    }

    public override fun onDestroy() {
        mPresenter.onDetach()
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_update -> {
                mPresenter.getWeather()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun setTemperature(temperature: String?) {
        txtTemperature.text = temperature
    }

    override fun setWind(wind: String?) {
        txtWind.text = wind
    }

    override fun setPlace(place: String?) {
        txtPlace.text = place
    }

    override fun setPicture(img: Bitmap?) {
        this@MainActivity.runOnUiThread {
            imgTemperature.setImageBitmap(img)
        }
    }

    @OnClick(R.id.btn_update_weather)
    fun onUpdateWeatherClick() {
        mPresenter.getWeather()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == Constants.GET_ACCESS_COARSE_LOCATION_REQUEST ||
            requestCode == Constants.GET_ACCESS_FINE_LOCATION_REQUEST ||
            requestCode == Constants.LOCATION_SOURCE_SETTINGS_RESULT) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                mPresenter.getWeather()
            }
        }
    }

    override fun openLocationSettings() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivityForResult(intent, Constants.LOCATION_SOURCE_SETTINGS_RESULT)
    }

    override fun setPbVisibility(visibility: Int) {
        this@MainActivity.runOnUiThread {
            pb.visibility = visibility
        }
    }

    override fun onResume() {
        super.onResume()
        mPresenter.startCheckWeather()
    }

    override fun onStop() {
        super.onStop()
        mPresenter.stopCheckWeather()
    }

}
