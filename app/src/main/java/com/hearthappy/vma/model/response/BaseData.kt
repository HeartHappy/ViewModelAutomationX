package com.hearthappy.vma.model.response


data class BaseData<T : Any>(val code: Int, val message: String, val result: T)