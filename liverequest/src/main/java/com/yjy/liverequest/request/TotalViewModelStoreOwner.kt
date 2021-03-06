package com.vanke.libvanke.net.request

import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2020/11/12
 *     desc   :
 *     version: 1.0
 * </pre>
 */
object TotalViewModelStoreOwner : ViewModelStoreOwner {
    private var totalViewStore: ViewModelStore = ViewModelStore()

    override fun getViewModelStore(): ViewModelStore {
        return totalViewStore
    }
}