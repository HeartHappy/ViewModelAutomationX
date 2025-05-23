package com.hearthappy.vma.model.response

import com.hearthappy.annotations.storage.DataStore
import com.hearthappy.annotations.storage.DataWrite
import com.hearthappy.annotations.storage.ObjectRelation

@DataStore("user_image_table",false)
data class ResImages(
    @DataWrite("code")
    val code: Int,

    @DataWrite("msg")
    val message: String,
    @ObjectRelation
    val result: Result
) {
    data class Result(
        val data: List<Data>,
        @DataWrite("total")
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