package com.hearthappy.vma.model.testmodel


/**
 *功能：好友页面
 *作者：
 *时间：
 **/
data class FriendsBean(var topList:MutableList<FriendUserBean>?=null,
                       var myList:MutableList<FriendUserBean>?=null,
                       var count:String="0",
                       var isPin:Int = 1,
)

data class FriendUserBean(var top:Int = 0,
                          var user_id:String = "",
                          var user_number:String = "",
                          var avatar:String = "",
                          var nickname:String = "",
                          var sex:Int = 0,
                          var remark:String = "",
                          var isChoose:Boolean = false
    )