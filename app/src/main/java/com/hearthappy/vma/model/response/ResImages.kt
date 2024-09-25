package com.hearthappy.vma.model.response

import com.hearthappy.annotations.storage.DataStore
import com.hearthappy.annotations.storage.DataStoreStorage

@DataStore("user_info")
data class ResImages(
    val code: Int,
    @DataStoreStorage("MSG")
    val message: String,
    val result: Result
) {
    data class Result(
        val list: List<Data>,
        val total: Int
    ) {
        data class Data(
            val id: Int,
            val title: String,
            val type: String,
            val url: String
        )
    }
}