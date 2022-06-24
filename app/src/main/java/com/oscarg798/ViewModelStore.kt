package com.oscarg798

import androidx.collection.LruCache
import androidx.lifecycle.ViewModel

class ViewModelStore {

    private val cachedViewModels = LruCache<String, ViewModel>(5)

    fun <T : ViewModel> get(key: String, create: () -> T) =
        cachedViewModels[key] ?: create.invoke().also {
            cachedViewModels.put(key, it)
        }
}