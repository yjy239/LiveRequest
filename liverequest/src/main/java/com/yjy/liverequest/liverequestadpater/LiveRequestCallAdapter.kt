package com.yjy.liverequest.liverequestadpater

import com.yjy.liverequest.request.DelayLiveRequest
import com.yjy.liverequest.request.LiveRequest
import com.yjy.liverequest.R
import retrofit2.Call
import retrofit2.CallAdapter
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
class LiveRequestCallAdapter(val responseType: Type) : CallAdapter<R, LiveRequest<R>> {
    override fun responseType(): Type {
        return responseType
    }

    override fun adapt(call: Call<R>): LiveRequest<R> {
        // 每一次尝试借助Retrofit 生成，而不是介入其中的流程
        return DelayLiveRequest<R>(call);
    }
}