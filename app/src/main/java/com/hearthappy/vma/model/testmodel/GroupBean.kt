package com.hearthappy.vma.model.testmodel


/**
 *功能：我的群列表bean类
 *作者：
 *时间：
 **/
data class GroupBean(var createList:MutableList<GroupUserBean>?=null,
                     var joinList:MutableList<GroupUserBean>?=null,
                     var isPin:Int = 1,
                     var count:String = "0"
)

data class GroupUserBean(var id:String = "",
                         var owner_id:String = "",
                         var group_name:String = "",
                         var group_avatar:String = "",
                         var group_desc:String = "",
                         var user_count:String = "",
                         var user_max:String = "",
                         var is_public:String = "",
                         var join_approval:String = "",
                         var group_alias:String = "",
                         var c_time:String = "",
                         var u_time:String = "",
                         var d_time:String = ""
)