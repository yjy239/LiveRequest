package com.yjy.liverequest.liverequestadpater

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2020/11/13
 *     desc   :
 *     version: 1.0
 * </pre>
 */
object ApplicationLifecycleOwner : LifecycleOwner {
    private val mLifecycleRegistry:LifecycleRegistry = LifecycleRegistry(this)

    init{
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    override fun getLifecycle(): Lifecycle {
        return mLifecycleRegistry
    }


}