package com.hearthappy.vma_ktx.factory

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlin.reflect.KClass

inline fun <reified VM : ViewModel, API> Fragment.vma(api: API): Lazy<VM> {
    return VMAFragmentLazy(this, api, VM::class)
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