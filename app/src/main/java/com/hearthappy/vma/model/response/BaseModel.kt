package com.hearthappy.vma.model.response


data class BaseModel<T>(val code: Int, val message: String, val result: T?)