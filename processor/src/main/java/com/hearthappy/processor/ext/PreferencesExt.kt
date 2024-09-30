package com.hearthappy.processor.ext

import com.hearthappy.processor.exceptions.VMAAnalysisException
import com.squareup.kotlinpoet.ClassName

fun String.string2preferenceType(imports: MutableList<ClassName>): String {
    val type = when (this) {
        "kotlin.String"   -> DataStoreTypeNames.DataStoreStringPreferences.apply { imports.add(this) }
        "kotlin.String?"  -> DataStoreTypeNames.DataStoreStringPreferences.apply { imports.add(this) }
        "kotlin.Int"      -> DataStoreTypeNames.DataStoreIntPreferences.apply { imports.add(this) }
        "kotlin.Int?"     -> DataStoreTypeNames.DataStoreIntPreferences.apply { imports.add(this) }
        "kotlin.Boolean"  -> DataStoreTypeNames.DataStoreBooleanPreferences.apply { imports.add(this) }
        "kotlin.Boolean?" -> DataStoreTypeNames.DataStoreBooleanPreferences.apply { imports.add(this) }
        "kotlin.Long"     -> DataStoreTypeNames.DataStoreLongPreferences.apply { imports.add(this) }
        "kotlin.Long?"    -> DataStoreTypeNames.DataStoreLongPreferences.apply { imports.add(this) }
        "kotlin.Float"    -> DataStoreTypeNames.DataStoreFloatPreferences.apply { imports.add(this) }
        "kotlin.Float?"   -> DataStoreTypeNames.DataStoreFloatPreferences.apply { imports.add(this) }
        "kotlin.Double"   -> DataStoreTypeNames.DataStoreDoublePreferences.apply { imports.add(this) }
        "kotlin.Double?"  -> DataStoreTypeNames.DataStoreDoublePreferences.apply { imports.add(this) }
        else              -> throw VMAAnalysisException("Unknown type,$this")
    }
    return type.simpleName
}

fun String.defaultValue(): Any {
    return when (this) {
        "kotlin.String"   -> "\"\""
        "kotlin.String?"  -> "\"\""
        "kotlin.Int"      -> 0
        "kotlin.Int?"     -> 0
        "kotlin.Boolean"  -> false
        "kotlin.Boolean?" -> false
        "kotlin.Long"     -> "0L" //该字符串引用，如果0L、0f，都会被自动推到成0,导致需要强转
        "kotlin.Long?"    -> "0L"
        "kotlin.Float"    -> "0f"
        "kotlin.Float?"   -> "0f"
        "kotlin.Double"   -> "0.0"
        "kotlin.Double?"  -> "0.0"
        else              -> throw VMAAnalysisException("Unknown type,$this")
    }
}