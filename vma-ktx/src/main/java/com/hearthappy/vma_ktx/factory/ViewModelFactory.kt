package com.hearthappy.vma_ktx.factory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


/**
 * @author ChenRui
 * ClassDescription： ViewModel创建工厂
 * @param VM : ViewModel泛型
 * @param API Retrofit接口泛型
 * @property viewModelClass ViewModel类的class
 * @property apiService API Retrofit接口
 * @property app Application
 */
class ViewModelFactory<VM : ViewModel, API>(
    private val viewModelClass: Class<VM>,
    private val apiService: API,
    private val app: Application,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST") override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(viewModelClass)) {
            viewModelClass.constructors.firstOrNull { it.parameterTypes.size==2 }?.run {
                isAccessible  = true
                return  newInstance(apiService, app) as  T
            }
        //            return viewModelClass.constructors.firstOrNull { it.parameters.size == 2 }?.call(apiService, app) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}