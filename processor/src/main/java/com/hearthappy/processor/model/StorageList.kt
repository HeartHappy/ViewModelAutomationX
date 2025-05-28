package com.hearthappy.processor.model


class StorageList {
    var name: String? = null //文件key
    var genericT: String? = null
    var storageData: MutableList<StorageData> = mutableListOf()
}