package com.hearthappy.processor.model

import com.google.devtools.ksp.symbol.KSFile
import com.hearthappy.processor.ext.DataStoreTypeNames
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import kotlin.properties.Delegates

class DataStoreData {
    var name: String by Delegates.notNull()
    var propertyType: TypeName = DataStoreTypeNames.DataStore.parameterizedBy(DataStoreTypeNames.DataStorePreferencesCore)
    var containingFile: KSFile? = null
    var aggregating: Boolean = false
    var enabledLog: Boolean = false
}