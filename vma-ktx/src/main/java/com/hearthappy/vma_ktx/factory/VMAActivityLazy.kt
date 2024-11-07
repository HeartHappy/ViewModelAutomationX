package com.hearthappy.vma_ktx.factory

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlin.reflect.KClass

/**
 * @author ChenRui
 * @receiver AppCompatActivity
 * @param apiBlock 接收Retrofit 代理接口
 * @return Lazy<VM>
 */
inline fun <reified VM : ViewModel, API> AppCompatActivity.vma(crossinline apiBlock: () -> API): Lazy<VM> {
    return VMAActivityLazy(this, apiBlock(), VM::class)
}

inline fun <reified VM : ViewModel, API> Fragment.vma(crossinline apiBlock: () -> API): Lazy<VM> {
    return VMAFragmentLazy(this, apiBlock(), VM::class)
}

class VMAFragmentLazy<VM : ViewModel, API>(private val fragment: Fragment, private val apiClass: API, val vmClass: KClass<VM>) : Lazy<VM> {
    private var vm: VM? = null
    override val value: VM
        get() {
            return vm?.run {
                this
            } ?: run {
                val vmaFactory = fragment.activity?.run { ViewModelFactory(vmClass, apiClass, this.application) }
                ViewModelProvider(fragment.viewModelStore, vmaFactory!!)[vmClass.java].also { vm = it }
            }
        }

    override fun isInitialized(): Boolean = vm != null
}


class VMAActivityLazy<VM : ViewModel, API>(private val activity: AppCompatActivity, private val apiClass: API, val vmClass: KClass<VM>) : Lazy<VM> {
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