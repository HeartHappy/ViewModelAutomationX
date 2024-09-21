package com.hearthappy.ksp.model.response

data class ResultData<T>(
    val success : Boolean,
    val result : Int,
    val message : String,
    val data: T) {
    companion object{
        const val CODE_SUCCESS = 0
    }

//    fun apiData() : T{
//        if (result == CODE_SUCCESS) {
//            return data
//        }else{
//            throw (RuntimeException(result,message))
//        }
//    }
}