package com.openweather.ui.base

import android.annotation.TargetApi
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import butterknife.Unbinder
import com.openweather.OpenWeatherApplication
import com.openweather.di.components.ActivityComponent
import com.openweather.di.components.DaggerActivityComponent
import com.openweather.di.modules.ActivityModule

abstract class BaseActivity : AppCompatActivity(), MvpView {
    protected var activityComponent: ActivityComponent? = null
    protected var unBinder: Unbinder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent = DaggerActivityComponent.builder()
                .activityModule(ActivityModule(this))
                .applicationComponent(OpenWeatherApplication.get(this)?.applicationComponent)
                .build()
    }

    override fun onDestroy() {
        unBinder?.unbind()
        super.onDestroy()
    }

    protected fun makeAnimation(): Bundle? {
        return ActivityOptionsCompat.makeCustomAnimation(this, android.R.anim.fade_in, android.R.anim.fade_out).toBundle()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    override fun startActivity(intent: Intent?) {
        super.startActivity(intent, makeAnimation())
    }

    override fun startActivityForResult(intent: Intent?, requestCode: Int) {
        super.startActivityForResult(intent, requestCode, makeAnimation())
    }

    @TargetApi(Build.VERSION_CODES.M)
    override fun requestPermissionsSafely(permissions: Array<String>, requestCode: Int) {
        ActivityCompat.requestPermissions(this,permissions, requestCode)
    }

    @TargetApi(Build.VERSION_CODES.M)
    override fun hasPermission(permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }

    override fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun showMessage(res: Int) {
        showMessage(getString(res))
    }
}