package com.openweather.model.realm

import io.realm.RealmObject

open class WindRealm: RealmObject () {
	open var speed: Float = 0f
	open var deg: Int = 0
}