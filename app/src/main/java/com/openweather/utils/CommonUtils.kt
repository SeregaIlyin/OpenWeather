package com.openweather.utils

object CommonUtils {

    fun degToText(windDeg: Int): String {
        val deg = Math.floor(windDeg.toDouble())
        var result = "no data"

            if(deg in 360.0..21.0) {
                result = "N"
            }else if(deg in 22.0..44.0){
                result = "NNE"
            }else if(deg in 45.0..66.0){
                result = "NE"
            }else if(deg in 67.0..89.0){
                result = "ENE"
            }else if(deg in 90.0..111.0){
                result = "E"
            }else if(deg in 112.0..134.0){
                result = "ESE"
            }else if(deg in 135.0..156.0){
                result = "SE"
            }else if(deg in 157.0..179.0){
                result = "SSE"
            }else if(deg in 180.0..201.0){
                result = "S"
            }else if(deg in 202.0..224.0){
                result = "SSW"
            }else if(deg in 225.0..246.0){
                result = "SW"
            }else if(deg in 247.0..269.0){
                result = "WSW"
            }else if(deg in 270.0..291.0){
                result = "W"
            }else if(deg in 292.0..314.0){
                result = "WNW"
            }else if(deg in 315.0..336.0){
                result = "NW"
            }else if(deg in 337.0..359.0){
                result = "NNW"
            }

        return result
    }
}