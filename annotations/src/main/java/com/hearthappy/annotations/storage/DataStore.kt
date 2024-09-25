package com.hearthappy.annotations.storage


@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS)
annotation class DataStore(val name: String, val enableLog: Boolean = false, val aggregating: Boolean = false)