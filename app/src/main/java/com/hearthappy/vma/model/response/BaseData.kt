package com.hearthappy.vma.model.response

import com.hearthappy.annotations.storage.DataStore


data class BaseData<T:Any>(val code:Int,val message:String,val result:T)