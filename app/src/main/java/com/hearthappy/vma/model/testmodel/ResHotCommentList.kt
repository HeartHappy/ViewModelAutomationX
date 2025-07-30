package com.hearthappy.vma.model.testmodel

import java.io.Serializable

class ResHotCommentList : ArrayList<ResHotCommentList.ResHotCommentListItem>(){
    data class ResHotCommentListItem(
        var c_time: String = "",
        var content: String = "",
        var id: Int = 0,
        var is_author: Int = 0,
        var is_give_like: Int = 0,
        var support_num: Int = 0,
        var user: User = User(),
        var user_dynamic_id: Int = 0,
        var user_id: Int = 0
    ) : Serializable{
        data class User(
            var avatar: String = "",
            var c_time: String = "",
            var charm_level_icon: String = "",
            var id: Int = 0,
            var level: Int = 0,
            var level_icon: String = "",
            var nickname: String = "",
            var sex: Int = 0
        ): Serializable
    }
}