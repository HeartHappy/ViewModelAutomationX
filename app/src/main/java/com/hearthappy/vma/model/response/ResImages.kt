package com.hearthappy.vma.model.response

import com.hearthappy.annotations.storage.DataStore
import com.hearthappy.annotations.storage.DataWrite
import com.hearthappy.annotations.storage.ObjectRelation

@DataStore("user_image_table", false)
data class ResImages(val data: List<Data>, @DataWrite("total") val total: Int?, @ObjectRelation val data2: Data?) {
    data class Data(@DataWrite("id") val id: Int, @DataWrite("title") val title: String?, @DataWrite("type") val type: String, @DataWrite("url") val url: String)
}