package com.yjy.liverequest.request

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.yjy.liverequest.liverequestadpater.LiveViewModelProvider
import java.lang.reflect.InvocationHandler
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

/**
 * <pre>
 * author : yjy
 * e-mail : yujunyu12@gmail.com
 * time   : 2020/11/12
 * desc   :
 * version: 1.0
</pre> *
 */
internal class LiveInvokeHandler<T : Any>(originApiService: T,private val api: Class<T>,viewStoreOwner: ViewModelStoreOwner?) : InvocationHandler {
    private val mOriginApiService: T
    private val mViewStoreOwner: ViewModelStoreOwner?

    @Throws(Throwable::class)
    override fun invoke(proxy: Any, method: Method, args: Array<Any>?): Any? {
        if (method.returnType != LiveRequest::class.java) {
            return null
        }
        if(mViewStoreOwner == null){
            throw RuntimeException("ViewModelStoreOwner is null")
        }

        val mutableLiveData =
                LiveViewModelProvider(mViewStoreOwner).getLiveData(method,api, MutableLiveData::class.java)

        // LiveRequest 和接口是一一对应的
        var realLiveRequest = LiveViewModelProvider(mViewStoreOwner, object : ViewModelProvider.Factory {
            override fun <R : ViewModel?> create(modelClass: Class<R>): R {
                return try {
                    modelClass.getConstructor(MutableLiveData::class.java, Any::class.java,ViewModelStoreOwner::class.java,
                            Method::class.java, Array<Any>::class.java,Class::class.java)
                            .newInstance(mutableLiveData, proxy, mViewStoreOwner,method, args,api)
                } catch (e: NoSuchMethodException) {
                    throw RuntimeException("Cannot create an instance of \$modelClass", e)
                } catch (e: InvocationTargetException) {
                    throw RuntimeException("Cannot create an instance of \$modelClass", e)
                } catch (e: IllegalAccessException) {
                    throw RuntimeException("Cannot create an instance of \$modelClass", e)
                } catch (e: InstantiationException) {
                    throw RuntimeException("Cannot create an instance of \$modelClass", e)
                }
            }
        }).get(method, RealLiveRequest::class.java) as RealLiveRequest<T>

        val delayRequest = ReflectUtils.invokeMethod(method, mOriginApiService, args) as? DelayLiveRequest<T>

        if(realLiveRequest.isReady){
            //确定了参数
            val index = realLiveRequest.pushRequest(delayRequest)
            //
            realLiveRequest.requestInternal(Args(index,args))
        }else{
            realLiveRequest.isReady = true
        }

        return realLiveRequest
    }

    init {
        mOriginApiService = originApiService
        mViewStoreOwner = viewStoreOwner
    }
}