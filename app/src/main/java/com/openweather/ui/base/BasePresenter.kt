package com.openweather.ui.base

import io.reactivex.disposables.CompositeDisposable
import okhttp3.ResponseBody
import org.json.JSONObject
import javax.inject.Inject


open class BasePresenter<V : MvpView?> @Inject constructor(private val compositeDisposable: CompositeDisposable?) : MvpPresenter<V?> {
    protected var mvpView: V? = null

    override fun onAttach(mvpView: V?) {
        this.mvpView = mvpView
    }

    override fun onDetach() {
        mvpView = null
        compositeDisposable?.dispose()
    }

    protected fun getCompositeDisposable(): CompositeDisposable? {
        return compositeDisposable
    }

    override fun doOnFailure(t: Throwable) {
        val s = "An error [" + t.message + "] occurred"
        t.printStackTrace()
        mvpView?.showMessage(s)
    }

    override fun processErrorBody(errorBody: ResponseBody?) {
        try {
            val jObjError = JSONObject(errorBody?.string()!!)
            mvpView?.showMessage(jObjError.getJSONObject("error").getString("message"))
        } catch (e: Exception) {
            e.printStackTrace()
            mvpView?.showMessage(e.message!!)
        }
    }
}