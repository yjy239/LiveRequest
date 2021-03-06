package com.yjy.liverequest.request.error
import com.yjy.liverequest.request.ObtainRequest

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2020/11/13
 *     desc   :
 *     version: 1.0
 * </pre>
 */
interface IErrorHandler {
    fun convertException(throwable: Throwable?): Throwable?
    fun <T:Any> onHandle(requestHolder: ObtainRequest<T>?, throwable: Throwable?)
}