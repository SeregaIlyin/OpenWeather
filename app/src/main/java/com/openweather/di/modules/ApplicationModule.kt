package com.openweather.di.modules

import android.app.Application
import android.content.Context
import com.openweather.di.annotation.ApplicationContext
import dagger.Module
import dagger.Provides

@Module
class ApplicationModule(private val mApplication: Application) {
    @Provides
    fun provideApplication(): Application {
        return mApplication
    }

    @Provides
    @ApplicationContext
    fun provideContext(): Context {
        return mApplication
    }

}