package com.yjy.liverequest.liverequestadpater

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.WildcardType

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2020/11/12
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class LiveViewModelProvider : ViewModelProvider {

    val viewModelStoreOwner:ViewModelStoreOwner;

    constructor(store: ViewModelStoreOwner): super(store){
        viewModelStoreOwner = store
    }

    constructor(store: ViewModelStoreOwner,factory: Factory):super(store,factory){
        viewModelStoreOwner = store
    }


    fun <T : ViewModel?> get(method: Method, modelClass: Class<T>): T {
        val responseType= method.genericReturnType as ParameterizedType;
        val key = "${getParameterUpperBound(0,responseType)}${method.name}${method.parameterTypes.map { 
            it.name
        }.toString()}${modelClass.canonicalName}"

        return super.get(key,modelClass);
    }


    fun <T : ViewModel?> get(clazz: Class<*>, modelClass: Class<T>): T {
        val key = "DEFAULT_KEY:${clazz.canonicalName}"
        return super.get(key,modelClass);
    }



    fun getParameterUpperBound(index: Int, type: ParameterizedType): Type? {
        val types = type.actualTypeArguments
        require(!(index < 0 || index >= types.size)) { "Index " + index + " not in range [0," + types.size + ") for " + type }
        val paramType = types[index]
        return if (paramType is WildcardType) {
            paramType.upperBounds[0]
        } else paramType
    }

    fun getLiveData(method: Method, serviceClazz:Class<*>,modelClass: Class<MutableLiveData<*>>): MutableLiveData<Any>? {
        //return get(method,modelClass)
        val responseType= method.genericReturnType as ParameterizedType;
        val key = "${getParameterUpperBound(0,responseType)}${method.name}${method.parameterTypes.map {
            it.name
        }.toString()}${modelClass.canonicalName}"

        val liveModel = get(serviceClazz, LiveModel::class.java);

        if(liveModel.liveDataMap.get(key) == null){
            val newLiveData = MutableLiveData<Any>()
            liveModel.liveDataMap.put(key,MutableLiveData<Any>())
            return newLiveData
        }else{
            return liveModel.liveDataMap.get(key)
        }
    }


}