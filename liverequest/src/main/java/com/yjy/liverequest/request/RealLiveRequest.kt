package com.yjy.liverequest.request

import android.view.ViewGroup
import androidx.lifecycle.*
import com.google.gson.reflect.TypeToken
import com.yjy.liverequest.liverequestadpater.LiveModelConfig
import com.yjy.liverequest.request.error.IErrorHandler
import com.yjy.liverequest.request.*
import com.yjy.liverequest.view.ILoadingStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import retrofit2.*
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2020/11/11
 *     desc   :
 *     version: 1.0
 * </pre>
 */

class RealLiveRequest<T : Any>(private val liveData: MutableLiveData<T>, private val mOriginApiService: Any,
                               private val mViewStoreOwner: ViewModelStoreOwner,
                               private val method: Method, private val args: Array<out Any>?,
                               private val api: Class<*>) : ViewModel(), LiveRequest<T> {
    var mArgs: Array<out Any>? = args

    var isReady: Boolean = false
        @Synchronized
        set(value) {
            field = value
        }


    // var mCache: Boolean = false
    var mErrorHandler: IErrorHandler? = LiveModelConfig.errorHandler
    var mViewReplacer: ILoadingStatus? = null
    var mViewStatus: Int = LiveRequest.NONE

    var isHandleError: Boolean = true

    private var isShowToast = true

    private var onFail: ((e: Throwable?) -> Unit)? = null

    private var onEnd: (() -> Unit)? = null

    internal val delayLiveRequestMap: ConcurrentHashMap<Long, DelayLiveRequest<T>?> = ConcurrentHashMap()

    private val mRequestMap: ConcurrentHashMap<Long, ObtainRequest<T>?> = ConcurrentHashMap()

    protected var mCacheId:String? = null

    protected var mCacheConfig: ICache? = LiveModelConfig.cacheConfig

    protected var mCacheMode:Int = ICache.NO_CACHE

    protected var errorView: ViewGroup? = null

    private var requestId:Long = 0;

    override fun isShowToast(): Boolean {
        return isShowToast
    }


    override fun getLiveData(): MutableLiveData<T> {
        return liveData
    }


    @Synchronized
    internal fun pushRequest(requestQueue: DelayLiveRequest<T>?): Long {
        val index = requestId
        delayLiveRequestMap.put(index, requestQueue)
        requestId++
        return index
    }


    private fun toRequest(): ObtainRequest<T> {
        return ObtainRequest(this, mOriginApiService, method,
                mArgs, mCacheMode, mErrorHandler,errorView,
                mViewReplacer, mViewStatus, isHandleError, isShowToast, onFail, onEnd,api,mCacheId,mCacheConfig)
    }


    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        liveData.observe(owner, observer)
    }


    override fun cache(cache: Int, id: String): LiveRequest<T> {
        mCacheMode = cache
        mCacheId = id
        return this
    }

    override fun errorHandler(handler: IErrorHandler): LiveRequest<T> {
        mErrorHandler = handler
        return this
    }

    override fun loadingReplacer(viewReplacer: ILoadingStatus?): LiveRequest<T> {
        mViewReplacer = viewReplacer
        return this
    }


    /**
     * 处理异常
     */
    override fun handleError(handle: Boolean, view: ViewGroup): LiveRequest<T> {
        isHandleError = handle
        errorView = view
        return this
    }

    /**
     * 视图loading 状态
     */
    override fun viewStatus(@LiveRequest.ViewStatus status: Int): LiveRequest<T> {
        mViewStatus = status
        return this
    }

    override fun onCleared() {
        super.onCleared()
        mRequestMap.forEach { entry ->
            entry.value?.onClear()
        }
        mViewReplacer = null

    }

    override fun showToast(show: Boolean): LiveRequest<T> {
        isShowToast = show
        return this
    }

    override fun getViewStatus(): Int {
        return mViewStatus
    }

    override fun getLoadingReplacer(): ILoadingStatus? {
        return mViewReplacer
    }

    override fun fail(fail: (e: Throwable?) -> Unit): LiveRequest<T> {
        onFail = fail
        return this
    }

    override fun end(endCallback: () -> Unit): LiveRequest<T> {
        onEnd = endCallback
        return this
    }

    internal fun retryRequest(request: ObtainRequest<T>?) {
        mRequestMap[requestId] = request
    }


    // 请求的时候，把参数固定起来，并进行处理
    @Synchronized
    override fun request(vararg args: Any) {
        if (!isReady) {
            return
        }
        if (LiveModelConfig.isDebug) {
            //默认无长度
            if (mArgs == null && args != null) {
                throw IllegalArgumentException("参数长度异常")
            }

            //长度不一致
            if (mArgs != null && args != null) {
                if (mArgs!!.size == 0 && args.size != 0) {
                    throw IllegalArgumentException("参数长度异常")
                }

                if (args.isNotEmpty() && mArgs!!.isNotEmpty() && args.size != mArgs!!.size) {
                    throw IllegalArgumentException("参数长度异常")
                }

                // 长度的类型不一致
                if (args.size == mArgs!!.size) {
                    for (index in 0..mArgs!!.size - 1) {
                        if (args[index].javaClass != mArgs!![index].javaClass) {
                            throw IllegalArgumentException("参数类型异常")
                        }
                    }
                }

            }
        }

        mRequestMap[requestId] = toRequest()

        ReflectUtils.invokeMethod(method, mOriginApiService, if (args.isEmpty()) mArgs else args)
    }


    internal fun requestInternal(args: Args) {
        viewModelScope.launch {
            showLoading(args.index)

            var data: T? = null

            mRequestMap[args.index]?.let {
                if (it.mCacheMode != ICache.NO_CACHE) {
                    //先获取缓存
                    var cacheData:T? = getCache(it.mCacher,it.mCacheId)

                    when(it.mCacheMode){
                        ICache.FIRST_CACHE ->{
                            if(cacheData != null){
                                dismissLoading(args.index)
                                viewModelScope.launch{
                                    // 刷新缓存数据
                                    this@RealLiveRequest.liveData.value = data
                                }
                            }

                            data = withContext(Dispatchers.IO) {
                                val resData  = requestNet(args)
                                saveCache(it.mCacher,it.mCacheId,resData)
                                resData
                            }

                        }

                        ICache.ONLY_CACHE ->{
                            if (cacheData == null) {
                                //没有从网络中获取
                                try {
                                    data = withContext(Dispatchers.IO) {
                                        val resData  = requestNet(args)
                                        saveCache(it.mCacher,it.mCacheId,resData)
                                        resData
                                    }


                                } catch (e: Exception) {
                                    dismissLoading(args.index)
                                    handleError(args.index, e)
                                }
                            }else{
                                data = cacheData
                            }
                        }
                    }


                } else {
                    // 只从网络获取
                    try {
                        data = withContext(Dispatchers.IO) {
                            requestNet(args)
                        }
                    } catch (e: Exception) {
                        dismissLoading(args.index)
                        handleError(args.index, e)
                    }
                }
            }



            if (data is Exception) {
                // 可能是异常,否则忽略，不保存异常数据
                handleError(args.index, data as Exception)
            } else if (data != null) {
                this@RealLiveRequest.liveData.value = data
            }

            mRequestMap[args.index]?.let {
                it.onEnd?.let {
                    it()
                }

                it.onClear()
            }


            mRequestMap.remove(args.index)
        }

    }

    private fun handleError(index: Long, e: Throwable?) {
        viewModelScope.launch {
            mRequestMap[index]?.let {
                val throwable = it.mErrorHandler?.convertException(e)
                if (it.isHandleError) {
                    it.mErrorHandler?.onHandle(mRequestMap[index], throwable)
                }

                it.onFail?.let {
                    it(e)
                }


            }
        }


    }

    private suspend fun <T> getCache(cache: ICache?, id:String?): T? {
        val typeToken = object : TypeToken<T>(){};

        val cache = withContext(Dispatchers.IO){
            cache?.getCache(id,typeToken.type) as T?
        }

        return cache
    }

    private suspend fun <T> saveCache(cache: ICache?, id:String?, data:T): Boolean {

        val isSuccess = withContext(Dispatchers.IO){
            cache?.saveCache(id,data)
        }
        return isSuccess?:false
    }

    private fun showLoading(index: Long) {
        when (mRequestMap[index]?.viewStatus) {
            LiveRequest.VIEW -> {
                mRequestMap[index]?.viewReplacer?.showLoading("")
            }

            LiveRequest.DIALOG -> {
                mRequestMap[index]?.viewReplacer?.showProgressDialog()
            }

            else -> {

            }
        }
    }


    private fun dismissLoading(index: Long) {
        when (mRequestMap[index]?.viewStatus) {
            LiveRequest.VIEW -> {
                mRequestMap[index]?.viewReplacer?.restore()
            }

            LiveRequest.DIALOG -> {
                mRequestMap[index]?.viewReplacer?.dismissProgressDialog()
            }

            else -> {

            }
        }
    }


    internal suspend fun requestNet(args: Args): T? {
        return suspendCancellableCoroutine { continuation ->
            continuation.invokeOnCancellation {
                delayLiveRequestMap[args.index]?.call?.cancel()
            }


            delayLiveRequestMap[args.index]?.call?.enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body == null) {
                            val invocation = call.request().tag(Invocation::class.java)!!
                            val method = invocation.method()
                            val e = KotlinNullPointerException("Response from " +
                                    method.declaringClass.name +
                                    '.' +
                                    method.name +
                                    " was null but response body type was declared as non-null")
                            continuation.resumeWithException(e)
                            delayLiveRequestMap.remove(args.index)
                        } else {
                            continuation.resume(body)
                            delayLiveRequestMap.remove(args.index)
                        }

                    } else {
                        continuation.resumeWithException(HttpException(response))
                        delayLiveRequestMap.remove(args.index)
                    }


                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    continuation.resumeWithException(t)
                    delayLiveRequestMap.remove(args.index)
                }
            })
        }
    }

    override fun clearLiveData() {
        liveData.value = null
    }


}


