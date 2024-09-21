package com.hearthappy.annotations


/**
 * @author ChenRui
 * @Email  1096885636@qq.com
 * @param fileName String Generate ViewModel file name
 * @param enableLog Boolean Enable Logging
 * @param aggregating Boolean true: aggregation false: isolation
 * @constructor
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class ViewModelAutomation(
    val fileName: String,
    val enableLog: Boolean = false,
    val aggregating: Boolean = false
)
