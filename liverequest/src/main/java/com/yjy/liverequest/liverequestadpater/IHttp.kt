package com.yjy.liverequest.liverequestadpater

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2020/11/13
 *     desc   :
 *     version: 1.0
 * </pre>
 */
interface IHttp {
    fun <T>getApi(clazz: Class<T>) : T
}