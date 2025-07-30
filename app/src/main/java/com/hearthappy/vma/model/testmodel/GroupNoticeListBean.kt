package com.hearthappy.vma.model.testmodel

/**
 *功能：群消息通知bean类
 *作者：
 *时间：
 **/
data class GroupNoticeListBean(var list:MutableList<GroupNoticeBean>?=null,
                               var next:Int = 0,
                               var count:Int = 0
    )
data class GroupNoticeBean(var id:String="",
                           var group_id:String ="",
                           var type:String = "",
                           var sender_id:String="",
                           var receiver_id:String = "",
                           var content:String = "",
                           var status:String ="",
                           var handled_time:String="",
                           var is_read:String = "",
                           var c_time:String ="",
                           var u_time:String = "",
                           var d_time:String = "",
                           var group: GroupItemBean?=null
)
data class GroupItemBean(var id:String = "",
                     var group_name:String = "",
                     var group_avatar:String = "")