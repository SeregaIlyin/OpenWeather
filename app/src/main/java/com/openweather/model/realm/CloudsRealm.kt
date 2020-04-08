package com.openweather.model.realm

import io.realm.RealmObject

open class CloudsRealm: RealmObject(){
	open var all: Int = 0
}