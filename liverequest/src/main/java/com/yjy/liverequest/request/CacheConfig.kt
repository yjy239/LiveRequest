package com.yjy.liverequest.request

import java.lang.reflect.Type

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2021/02/01
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public interface ICache{
    suspend fun <T> getCache(id:String?,type: Type):T?

    suspend fun <T> saveCache(id:String?,data: T):Boolean

    companion object{
        val NO_CACHE: Int  = 0

        val FIRST_CACHE: Int = 1

        val ONLY_CACHE: Int  = 1
    }


}






