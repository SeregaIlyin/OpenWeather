package com.openweather.di.components

import com.openweather.di.annotation.PerActivity
import com.openweather.di.modules.ActivityModule
import com.openweather.ui.main.MainActivity
import dagger.Component

@PerActivity
@Component(dependencies = [ApplicationComponent::class], modules = [ActivityModule::class])
interface ActivityComponent {
    fun inject(activity: MainActivity)
}