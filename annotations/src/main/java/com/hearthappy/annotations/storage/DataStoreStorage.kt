package com.hearthappy.annotations.storage


@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.PROPERTY)
annotation class DataStoreStorage(val key: String)
