package com.hearthappy.vma.model.testmodel

import java.io.Serializable

data class NoticeUnreadBean(var dynamic:String="0",
                            var comment:String="0",
                            var clickLike:String="0",
                            var fans:String="0",
                            var visit:String="0",
                            var act : String = "0",
                            var system:String="0",val system_msg: SystemMsg
) : Serializable

class SystemMsg(
    val c_time: String,
    val content: String,
    val ext: Ext,
    val img: String,
    val notice_id: Int,
    val scene: Int,
    val title: String,
    val url: String,
    val user_id: Int,
    val c_time_format: String
): Serializable

data class Ext(
    var user_nickname: String,
    var user_id: String,
    var room_name: String,
    var gift_title: String,
    var gift_num: Int,
    var img: String,
    var room_id: String,
    var nobility_name: String,
    var buy_type: String,
    var level: String,
    var type: String,
    val type_title:String,
    val price:String,
)