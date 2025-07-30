package com.hearthappy.vma.model.testmodel


data class DynamicLikeDto(
    val count: Int,
    val next: Int,
    val record: List<RecordBean>
)

data class RecordBean(
    val c_time: String,
    val id: Int,
    val user_id: Int,
    val userinfo: UserBean
)

data class UserBean(
    val avatar: String,
    val c_time: String,
    val id: Int,
    val nickname: String,
    val user_number:String,
    val sex: Int
)