package com.openweather.ui.base

import okhttp3.ResponseBody

interface MvpPresenter<V : MvpView?> {
    fun onAttach(mvpView: V?)
    fun onDetach()
    fun doOnFailure(t: Throwable)
    fun processErrorBody(errorBody: ResponseBody?)
}