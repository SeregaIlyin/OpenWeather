package com.openweather.di.components

import android.content.Context
import com.openweather.OpenWeatherApplication
import com.openweather.di.annotation.ApplicationContext
import com.openweather.di.modules.ApplicationModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class])
interface ApplicationComponent {
    open fun inject(openWeatherApplication: OpenWeatherApplication)
    @ApplicationContext
    open fun context(): Context
}