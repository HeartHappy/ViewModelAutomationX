package com.hearthappy.vma.model.response

import com.hearthappy.annotations.storage.DataStore
import com.hearthappy.annotations.storage.DataWrite

@DataStore("user_info")//泛型问题
data class ResSentences(@DataWrite("from") var from: String = "", @DataWrite("name") var name: String = "")