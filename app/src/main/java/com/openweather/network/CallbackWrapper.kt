package com.openweather.network

import com.openweather.model.BaseResponse
import com.openweather.ui.base.MvpPresenter
import io.reactivex.observers.DisposableObserver
import retrofit2.HttpException

abstract class CallbackWrapper<T: BaseResponse>(private val presenter: MvpPresenter<*>) :
    DisposableObserver<T>() {
    protected abstract fun onSuccess(t: T)
    override fun onNext(t: T) {
        onSuccess(t)
    }

    override fun onError(e: Throwable) {
        if (e is HttpException) {
            val responseBody = e.response().errorBody()
            presenter.processErrorBody(responseBody)
        } else {
            presenter.doOnFailure(e)
        }
    }

    override fun onComplete() {}
}