package com.hearthappy.processor.ext

import com.hearthappy.processor.exceptions.VMAAnalysisException
import com.squareup.kotlinpoet.ClassName

fun String.string2preferenceType(imports: MutableList<ClassName>): String {
    val type = when (this) {
        "kotlin.String"  -> DataStoreTypeNames.DataStoreStringPreferences.apply { imports.add(this) }
        "kotlin.Int"     -> DataStoreTypeNames.DataStoreIntPreferences.apply { imports.add(this) }
        "kotlin.Boolean" -> DataStoreTypeNames.DataStoreBooleanPreferences.apply { imports.add(this) }
        "kotlin.Long"    -> DataStoreTypeNames.DataStoreLongPreferences.apply { imports.add(this) }
        else             -> throw VMAAnalysisException("Unknown type,$this")
    }
    return type.simpleName
}