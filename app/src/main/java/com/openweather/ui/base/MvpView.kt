package com.openweather.ui.base

interface MvpView {
    fun hasPermission(permission: String): Boolean
    fun requestPermissionsSafely(permissions: Array<String>, requestCode: Int)
    fun showMessage(message: String)
    fun showMessage(res: Int)
}