package com.hearthappy.vma_ktx.factory

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlin.reflect.KClass

/**
 * @author ChenRui
 * @receiver AppCompatActivity
 * @param apiBlock 接收Retrofit 代理接口
 * @return Lazy<VM>
 */
inline fun <reified VM : ViewModel, API> AppCompatActivity.vma(crossinline apiBlock: () -> API): Lazy<VM> {
    return VMALazy(this, apiBlock(), VM::class)
}

class VMALazy<VM : ViewModel, API>(private val activity: AppCompatActivity, private val apiClass: API, val vmClass: KClass<VM>) : Lazy<VM> {
    private var vm: VM? = null
    override val value: VM
        get() {
            return vm?.run {
                this
            } ?: run {
                val vmaFactory = ViewModelFactory(vmClass, apiClass, activity.application)
                ViewModelProvider(activity, vmaFactory)[vmClass.java].also { vm = it }
            }
        }

    override fun isInitialized(): Boolean = vm != null

}