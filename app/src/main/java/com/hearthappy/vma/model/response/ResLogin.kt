package com.hearthappy.vma.model.response

import com.hearthappy.annotations.storage.DataStore
import com.hearthappy.annotations.storage.DataStoreStorage

@DataStore("user_preferences")
data class ResLogin(
    val code: Int,
    @DataStoreStorage("msg")
    val message: String,
    val result: Result?
) {
    data class Result(
        val account: String,
        val createdAt: String,
        val deletedAt: Any,
        val id: Int,
        val level: Int,
        val token: String,
        val updatedAt: String
    )
}