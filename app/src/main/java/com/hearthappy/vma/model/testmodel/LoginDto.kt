package com.hearthappy.vma.model.testmodel

import com.hearthappy.annotations.storage.DataStore
import com.hearthappy.annotations.storage.DataWrite

/**
 * @project : YinMan
 * @Description : 项目描述
 * @author : clb
 * @time : 2022/1/6
 */
@DataStore("login_info")
data class LoginDto(@DataWrite("token") var token: String, var update_user: Boolean)
