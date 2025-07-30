package com.hearthappy.vma.model.response

import com.hearthappy.annotations.storage.DataStore
import com.hearthappy.annotations.storage.DataWrite
import com.hearthappy.annotations.storage.ObjectRelation

@DataStore("user_data")
data class ResSentences(@DataWrite("from") var from: String = "", @DataWrite("name") var name: String = "")