package com.openweather.model.realm

import io.realm.RealmObject

open class CoordRealm: RealmObject(){
	open var lat: Double = 0.0
	open var lon: Double = 0.0
}