package com.openweather.di.modules

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.openweather.di.annotation.ActivityContext
import com.openweather.ui.base.BaseActivity
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable


@Module
class ActivityModule(private val baseActivity: BaseActivity) {
    @Provides
    fun provideActivity(): AppCompatActivity {
        return baseActivity
    }

    @Provides
    @ActivityContext
    fun providesContext(): Context {
        return baseActivity
    }

    @Provides
    fun provideCompositeDisposable(): CompositeDisposable {
        return CompositeDisposable()
    }

}