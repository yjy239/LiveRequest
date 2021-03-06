package com.yjy.liverequest.liverequestadpater

import com.yjy.liverequest.request.LiveRequest
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2020/11/09
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class LiveRequestCallAdapterFactory : CallAdapter.Factory() {

    override fun get(returnType: Type, annotations: Array<Annotation>, retrofit: Retrofit): CallAdapter<*, *>? {
        val rawType = getRawType(returnType)

        if(rawType !=  LiveRequest::class.java){
            return null;
        }

        //获取
        val liveRequestType = getParameterUpperBound(0, returnType as ParameterizedType)

        return LiveRequestCallAdapter(liveRequestType)
    }
}