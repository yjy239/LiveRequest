package com.yjy.liverequest.liverequestadpater

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.yjy.liverequest.request.LiveInvokeHandler
import com.vanke.libvanke.net.request.TotalViewModelStoreOwner
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Proxy

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2020/11/11
 *     desc   :
 *     version: 1.0
 * </pre>
 */
open class LiveModel<T : Any> constructor(httpManager: IHttp, owner: ViewModelStoreOwner, api: Class<T>) : ViewModel() {

    val apiService: T
    private var viewStoreOwner: ViewModelStoreOwner?

    var liveDataMap: HashMap<String, MutableLiveData<Any>> = HashMap()

    init {
        val originApiService: T = httpManager.getApi(api)
        viewStoreOwner = owner
        apiService = Proxy.newProxyInstance(api.classLoader, arrayOf(api), LiveInvokeHandler<T>(originApiService,api, viewStoreOwner)) as T
    }


    override fun onCleared() {
        super.onCleared()
        viewStoreOwner = null
    }

    class Builder constructor() {
        var mManager: IHttp? = null
        lateinit var mOwner: ViewModelStoreOwner

        fun setHttpManager(manager: IHttp) {
            mManager = manager
        }

        fun <T : Any> create(owner: ViewModelStoreOwner = totalViewStore, service: Class<T>): LiveModel<T> {

            mManager = mManager ?: null
            mOwner = owner

            // 一个apiService对应一个不同的LiveModel
            val liveModel = LiveViewModelProvider(mOwner, object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    return try {
                        Log.e("ddd","----------------")
                        modelClass.getConstructor(
                            IHttp::class.java,
                                ViewModelStoreOwner::class.java, Class::class.java).newInstance(mManager, mOwner, service)
                    } catch (e: NoSuchMethodException) {
                        throw RuntimeException("NoSuchMethodException Cannot create an instance of $modelClass", e)
                    } catch (e: IllegalAccessException) {
                        throw RuntimeException("IllegalAccessException Cannot create an instance of $modelClass", e)
                    } catch (e: InstantiationException) {
                        throw RuntimeException("InstantiationException Cannot create an instance of $modelClass", e)
                    } catch (e: InvocationTargetException) {
                        throw RuntimeException("InvocationTargetException Cannot create an instance of $modelClass", e)
                    }
                }

            }).get(service, LiveModel::class.java)

            return liveModel as LiveModel<T>
        }
    }


    companion object {
        val totalViewStore: ViewModelStoreOwner = TotalViewModelStoreOwner
    }

}