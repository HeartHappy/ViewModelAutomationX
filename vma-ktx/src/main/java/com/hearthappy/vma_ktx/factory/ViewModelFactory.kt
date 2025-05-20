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
            for (constructor in viewModelClass.constructors) {
                if (constructor.getParameterTypes().size === 2) {
                    // 确保构造函数可访问（处理 private 构造函数）
                    constructor.isAccessible = true
                    // 调用构造函数实例化 ViewModel（参数为 API 和 Application）
                    return constructor.newInstance(apiService, app) as T
                }
            }
//            return viewModelClass.constructors.firstOrNull { it.parameters.size == 2 }?.call(apiService, app) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}