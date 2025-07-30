package com.hearthappy.vma.model.testmodel

/**
 *功能：好友申请列表
 *作者：
 *时间：
 **/
data class FriendApplyBean(var c_time:String="",
                           var today:List<FriendBean> = listOf(),
                           var yesterday:List<FriendBean> = listOf(),
                           var threeDay:List<FriendBean> = listOf()
    )
data class FriendBean(var id:String = "",
                      var user_id:String = "",
                      var friend_id:String = "",
                      var status:Int=0,
                      var apply_time:String="",
                      var type:String = "",
                      var user_number:String="",
                      var sex:Int = 0,
                      var avatar:String="",
                      var nickname: String="",

    )