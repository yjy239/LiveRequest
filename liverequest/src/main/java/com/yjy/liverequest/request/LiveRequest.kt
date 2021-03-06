package com.yjy.liverequest.request

import android.view.ViewGroup
import androidx.annotation.IntDef
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.yjy.liverequest.request.error.IErrorHandler
import com.yjy.liverequest.view.ILoadingStatus

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2020/11/12
 *     desc   : LiveModel 的抽象请求
 *     version: 1.0
 * </pre>
 */
interface LiveRequest<T> {

    /**
     * 监听数据到来
     */
    fun observe(owner: LifecycleOwner, observer: Observer<in T>)

    /**
     * cache为true 有缓存优先缓存，有缓存就使用缓存不进行网络请求
     */
    fun cache(cache: Int = ICache.NO_CACHE, id: String): LiveRequest<T>

    /**
     * 是否处理异常，如果设置为false，一旦异常就不会有任何处理，并忽略不回调
     */
    fun handleError(handle: Boolean = true, view: ViewGroup): LiveRequest<T>

    /**
     * 异常处理器机制
     */
    fun errorHandler(handler: IErrorHandler): LiveRequest<T>

    /**
     * 视图loading逻辑
     */
    fun loadingReplacer(viewReplacer: ILoadingStatus?): LiveRequest<T>


    /**
     * 视图loading 状态
     */
    fun getLoadingReplacer(): ILoadingStatus?

    /**
     * 视图loading 状态
     */
    fun viewStatus(@ViewStatus status: Int): LiveRequest<T>

    /**
     * 视图loading 状态
     */
    fun getViewStatus():Int



    fun showToast(show:Boolean): LiveRequest<T>


    /**
     * 是否显示Toast
     */
    fun isShowToast():Boolean


    /**
     * 请求数据
     */
    fun request(vararg args: Any)

    /**
     * 特殊处理本次异常
     */
    fun fail(onFail:(e:Throwable?)->Unit): LiveRequest<T>

    /**
     * 执行结束回调
     */
    fun end(onEnd:()->Unit): LiveRequest<T>

    /**
     * 获取内部的LiveData
     */
    fun getLiveData(): MutableLiveData<T>

    /**
     * 清空数据
     */
    fun clearLiveData()



    companion object View{
        const val NONE: Int = 2
        const val VIEW: Int = 0
        const val DIALOG: Int = 1
    }

    @IntDef(NONE, VIEW, DIALOG)
    @Retention(value = AnnotationRetention.RUNTIME)
    @Target(AnnotationTarget.VALUE_PARAMETER)
    annotation class ViewStatus
}