package com.yjy.liverequest.request

import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import com.yjy.liverequest.request.error.IErrorHandler
import com.yjy.liverequest.view.ILoadingStatus
import java.lang.reflect.Method

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2020/11/16
 *     desc   : 保存当前请求的快照
 *     version: 1.0
 * </pre>
 */
class ObtainRequest<T:Any> internal constructor (
    val request: RealLiveRequest<T>, val mOriginApiService: Any,
    val method: Method, val args: Array<out Any>?,
    val mCacheMode:Int,
    val mErrorHandler: IErrorHandler?, var errorView: ViewGroup?,
    var viewReplacer: ILoadingStatus?, val viewStatus:Int,
    val isHandleError:Boolean, val isShowToast:Boolean,
    val onFail: ((e: Throwable?) -> Unit)?, val onEnd: (() -> Unit)? = null,
    private val api: Class<*>, val mCacheId:String?, val mCacher: ICache?)
    : ViewModel() {


    fun retry(){
        request.retryRequest(this)
        ReflectUtils.invokeMethod(method, mOriginApiService, args)
    }

    fun onClear() {

        viewReplacer = null
    }


}