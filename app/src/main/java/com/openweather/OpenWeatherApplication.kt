package com.openweather

import android.content.Context
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication
import com.openweather.di.components.ApplicationComponent
import com.openweather.di.components.DaggerApplicationComponent
import com.openweather.di.modules.ApplicationModule
import io.realm.Realm
import io.realm.RealmConfiguration

class OpenWeatherApplication: MultiDexApplication() {
    var applicationComponent: ApplicationComponent? = null

    override fun onCreate() {
        super.onCreate()

        applicationComponent = DaggerApplicationComponent
                .builder()
                .applicationModule(ApplicationModule(this))
                .build()

        applicationComponent?.inject(this)

        instance = this
        val builder = VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        Realm.init(this)
        val realmConfiguration = RealmConfiguration.Builder()
            .deleteRealmIfMigrationNeeded()
            .build()
        Realm.setDefaultConfiguration(realmConfiguration)
    }


    companion object {
        fun getInstance(): OpenWeatherApplication? {
            return instance
        }

        fun getContext(): Context? {
            return instance
        }

        private var instance: OpenWeatherApplication? = null
        operator fun get(context: Context?): OpenWeatherApplication? {
            return context?.getApplicationContext() as OpenWeatherApplication
        }
    }
}