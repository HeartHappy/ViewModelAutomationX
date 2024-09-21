package com.hearthappy.annotations

/**
 * @Author ChenRui
 * @Email  1096885636@qq.com
 * @param methodName String Function rename, default: API interface function name
 * @param propertyName String Rename the StateFlow property, default: function name + StateFlow name
 * @constructor
 */
//@Repeatable
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
annotation class BindStateFlow(
    val methodName: String = "",
    val propertyName: String = "",
)

