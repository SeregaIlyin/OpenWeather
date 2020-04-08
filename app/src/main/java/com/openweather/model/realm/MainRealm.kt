package com.openweather.model.realm

import io.realm.RealmObject

open class MainRealm: RealmObject () {
	open var temp: Double = 0.0
	open var feelsLike: Double = 0.0
	open var tempMin: Double = 0.0
	open var tempMax: Double = 0.0
	open var pressure: Int = 0
	open var humidity: Int = 0
}