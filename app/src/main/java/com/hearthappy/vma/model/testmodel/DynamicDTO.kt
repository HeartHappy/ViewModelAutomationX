package com.hearthappy.vma.model.testmodel

import java.io.Serializable


class DynamicDTO : Serializable {
    var c_time: String? = null
    var comment_num: Int? = null
    var content: String? = null
    var cover_img: String? = null
    var id: Int? = null
    var imgs: List<String>? = null
    var is_fork: Int? = null
    var is_give_like: Int? = null
    var is_show_location: Int? = null
    var popularity: Int? = null
    var proportion: String? = null
    var support: Int? = null
    var type_name: String? = null
    var user: UserDTO? = null
    var topic_id: String = ""
    var topic_title: String = ""
    var userDynamicLocation: UserDynamicLocationDTO? = null
    var user_id: Int? = null
    var video: String? = null
    var is_attention: Int? = null
    var in_live: Int? = null
    var in_live_room_id: String? = null
    var top_sort: Int = 0
    var head_portrait_box: String? = null
    var year: String? = null
    var hot_comment: ResHotCommentList? = null


    /**
     * //只在局部刷新的时候使用  0 不处理, 1 点赞刷新  2 评论数刷新
     */
    var type: Int = 0

    class ResHotComment : Serializable {
        var c_time: String? = null
        var content: String? = null
        var id: Int? = null
        var is_author: Int? = null
        var is_give_like: Int = 0
        var support_num: Int = 0
        var user: UserDTO? = null
        var user_dynamic_id: Int = 0
        var user_id: Int = 0
    }

    class UserDTO : Serializable {
        var avatar: String? = null
        var id: Int? = null
        var is_recommend: Int? = null
        var level: Int? = null
        var nickname: String? = null
        var icon: String? = null
        var sex: Int? = null
        var age: Int = 0
        var status: Int? = null
        var level_icon: String? = null
        var charm_level_icon: String? = null
        var charm_level: Int? = null
        var ifblack: Int? = null //0:拉黑 1:未拉黑
        var nobility_user: NobilityUserBean? = null

        var userVipLevel: UserVipLevelDTO? = null


        class UserVipLevelDTO : Serializable {
            var experience: Int? = null
            var icon: String? = null
            var id: Int? = null
            var level: Int? = null
        }


        class NobilityUserBean : Serializable {
            var nobility_id: String? = null
            var nobility_name: String? = null
            var nobility_level: String? = null
            var nobility_img: String? = null
            var end_time: Long? = null
            var end_time_format: String? = null
            var nobility_img_switch: Int = 0
        }
    }

    class UserDynamicLocationDTO : Serializable {
        var id: Int? = null
        var lat: String? = null
        var lng: String? = null
        var name: String? = null
        var user_dynamic_id: Int? = null
    }

}