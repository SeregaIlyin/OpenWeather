package com.openweather.model.realm

import io.realm.RealmObject

open class WeatherRealm: RealmObject (){
	open var id : Int = 0
	open var main : String = ""
	open var description : String = ""
	open var icon : String = ""
}
