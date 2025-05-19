package com.hearthappy.vma.model.response

import com.hearthappy.annotations.storage.DataStore
import com.hearthappy.annotations.storage.DataWrite

@DataStore("user_info")
data class ResLogin(
    val code: Int,
    @DataWrite("msg")
    val message: String,
    val result: Result?
) {
    data class Result(
        @DataWrite("account")
        val account: String,
        val createdAt: String,
        val deletedAt: Any,
        @DataWrite("id")
        val id: Int,
        val level: Int,
        @DataWrite("token")
        val token: String,
        val updatedAt: String
    )
}