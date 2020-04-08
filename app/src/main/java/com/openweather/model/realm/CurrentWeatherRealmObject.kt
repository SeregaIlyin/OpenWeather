package com.openweather.model.realm

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class CurrentWeatherRealmObject(): RealmObject(){
	@PrimaryKey
	open var uid : Long = 0
	open var coord : CoordRealm? = CoordRealm()
	open var weather : RealmList<WeatherRealm> = RealmList<WeatherRealm>()
	open var  base : String = ""
	open var main : MainRealm? = MainRealm()
	open var visibility : Int = 0
	open var wind : WindRealm? = WindRealm()
	open var clouds : CloudsRealm? = CloudsRealm()
	open var dt : Int = 0
	open var sys : SysRealm? = SysRealm()
	open var timezone : Int = 0
	open var id : Int = 0
	open var name : String = ""
	open var cod : Int = 0

}