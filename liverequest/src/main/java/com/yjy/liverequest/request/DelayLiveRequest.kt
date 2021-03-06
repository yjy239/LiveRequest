package com.yjy.liverequest.request

import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.yjy.liverequest.request.error.IErrorHandler
import com.yjy.liverequest.view.ILoadingStatus
import retrofit2.Call

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2020/11/12
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class DelayLiveRequest<T>(val call: Call<T>): LiveRequest<T> {

    override fun observe(owner: LifecycleOwner, observer: Observer<in T>){

    }

    override fun cache(cache: Int, id: String): LiveRequest<T> {
        return this
    }

    /**
     * 处理异常
     */
    override fun handleError(handle: Boolean, view: ViewGroup): LiveRequest<T> {
        return this
    }

    override fun errorHandler(handler: IErrorHandler): LiveRequest<T> {
        return this
    }

    override fun loadingReplacer(viewReplacer: ILoadingStatus?): LiveRequest<T> {
        return this
    }


    override fun viewStatus(@LiveRequest.ViewStatus status: Int): LiveRequest<T> {
        return this
    }

    override fun showToast(show: Boolean): LiveRequest<T> {
        return this
    }

    override fun getViewStatus(): Int {
        return LiveRequest.NONE
    }

    override fun getLoadingReplacer(): ILoadingStatus? {
        return null
    }

    override fun isShowToast(): Boolean {
        return true
    }


    override fun request(vararg args: Any) {

    }

    override fun fail(onFail: (e: Throwable?) -> Unit): LiveRequest<T> {
        return this
    }

    override fun end(onEnd: () -> Unit): LiveRequest<T> {
        return this
    }


    override fun getLiveData(): MutableLiveData<T> {
        return MutableLiveData()
    }

    override fun clearLiveData() {

    }


}