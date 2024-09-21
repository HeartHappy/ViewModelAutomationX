package com.hearthappy.annotations

/**
 * @Author ChenRui
 * @Email  1096885636@qq.com
 * @param methodName String Function rename, default: API interface function name
 * @param propertyName String LiveData property rename, default: function name + LiveData name
 * @constructor
 */
//@Repeatable
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
annotation class BindLiveData(
    val methodName: String = "",
    val propertyName: String = ""
)
