package com.hearthappy.vma_ktx.factory

import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.Factory
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.viewmodel.CreationExtras
import kotlin.reflect.KClass

@MainThread
inline fun <reified VM : ViewModel> Fragment.vmaFromActivity(noinline extrasProducer: (() -> CreationExtras)? = null, noinline factoryProducer: (() -> Factory)? = null): Lazy<VM> = createViewModelLazy(VM::class, { requireActivity().viewModelStore }, { extrasProducer?.invoke() ?: requireActivity().defaultViewModelCreationExtras }, factoryProducer ?: { requireActivity().defaultViewModelProviderFactory })

@MainThread
fun <VM : ViewModel> Fragment.createViewModelLazy(viewModelClass: KClass<VM>, storeProducer: () -> ViewModelStore, extrasProducer: () -> CreationExtras = { defaultViewModelCreationExtras }, factoryProducer: (() -> Factory)? = null

): Lazy<VM> {
    val factoryPromise = factoryProducer ?: {
        defaultViewModelProviderFactory
    }
    return VMAFragmentOneselfLazy(viewModelClass, storeProducer, factoryPromise, extrasProducer)
}

class VMAFragmentOneselfLazy<VM : ViewModel> @JvmOverloads constructor(private val viewModelClass: KClass<VM>, private val storeProducer: () -> ViewModelStore, private val factoryProducer: () -> ViewModelProvider.Factory, private val extrasProducer: () -> CreationExtras = { CreationExtras.Empty }) : Lazy<VM> {
    private var cached: VM? = null

    override val value: VM
        get() {
            val viewModel = cached
            return if (viewModel == null) {
                val factory = factoryProducer()
                val store = storeProducer()
                ViewModelProvider(store, factory, extrasProducer())[viewModelClass.java].also {
                    cached = it
                }
            } else {
                viewModel
            }
        }

    override fun isInitialized(): Boolean = cached != null
}