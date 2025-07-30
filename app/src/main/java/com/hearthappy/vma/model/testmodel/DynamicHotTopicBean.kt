package com.hearthappy.vma.model.testmodel

/**
 * Created Date: 2025/2/26
 * @author ChenRui
 * ClassDescription：热门话题
 */
data class DynamicHotTopicBean(var canyu: List<String> = listOf(), var list: List<Item0> = listOf()) {
    data class Item0(var id: Int = 0, var image: String = "", var title: String = "")
}