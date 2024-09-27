package com.hearthappy.annotations.storage


/**
 * Tag attributes and store using DataStore
 * @Author ChenRui
 * @Email  1096885636@qq.com
 * @param key String Storage key
 * @constructor
 */
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.PROPERTY)
annotation class DataWrite(val key: String)
