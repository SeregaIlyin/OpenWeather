package com.openweather.model.realm

import io.realm.RealmObject

open class SysRealm: RealmObject () {
	open var type: Int = 0
	open var id: Int = 0
	open var country: String = ""
	open var sunrise: Int = 0
	open var sunset: Int = 0
}