package com.hearthappy.vma.model.testmodel

/**
 *功能：群主操作bean类
 *作者：
 *时间：
 **/
data class GroupApplyStatusBean(var salt:String = "",
                                var sign:String = "",
                                var action:String = "",
                                var user_id:String = "",
                                var group_id:String = ""
)