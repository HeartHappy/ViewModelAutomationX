package com.hearthappy.vma.model.testmodel

class DynamicCommentDto {
    var count: Int? = null
    var next: Int? = null
    var record: List<ComnentDto>? = null

    class ComnentDto {
        var c_time: String? = null
        var content: String? = null
        var id: Int? = null
        var is_author: Int? = null //1就是作者
        var is_give_like: Int? = null
        var support_num: Int? = null
        var user: UserDTO? = null
        var user_id: Int? = null

        class UserDTO {
            var avatar: String? = null
            var level_icon: String? = null
            var charm_level_icon: String? = null
            var c_time: String? = null
            var id: Int? = null
            var nickname: String? = null
            var sex: Int? = null
        }
    }
}
