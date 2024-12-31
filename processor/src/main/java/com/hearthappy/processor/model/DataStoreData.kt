package com.hearthappy.processor.model

import com.google.devtools.ksp.symbol.KSFile
import com.hearthappy.processor.ext.DataStoreTypeNames
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import kotlin.properties.Delegates

/**
 *
 * @property name String 存储文件名
 * @property propertyType TypeName androidx.datastore.core.DataStore<androidx.datastore.preferences.core.Preferences>
 * @property containingFile KSFile? DataStore注解文件
 * @property aggregating Boolean 是否聚合
 * @property enabledLog Boolean 是否启用日志
 * @property storageMap MutableMap<String, String> key：大写存储属性名，value：小写属性名
 */
class DataStoreData {
    var name: String by Delegates.notNull()
    var propertyType: TypeName = DataStoreTypeNames.DataStore.parameterizedBy(DataStoreTypeNames.DataStorePreferencesCore)
    var containingFile: KSFile? = null
    var aggregating: Boolean = false
    var enabledLog: Boolean = false
    var storageMap = mutableMapOf<String, String>()

    override fun toString(): String {
        return "DataStoreData(name='$name', propertyType=$propertyType, containingFile=$containingFile, aggregating=$aggregating, enabledLog=$enabledLog,storageMap=$storageMap)"
    }

    companion object {
        fun mergeDuplicates(list: List<DataStoreData>): List<DataStoreData> {
            val map = mutableMapOf<String, DataStoreData>()
            list.forEach { data ->
                val existing = map[data.name]
                existing?.apply {
                    storageMap.putAll(data.storageMap)
                }?:apply { map[data.name] = data }
            }
            return map.values.toList()
        }
    }
}