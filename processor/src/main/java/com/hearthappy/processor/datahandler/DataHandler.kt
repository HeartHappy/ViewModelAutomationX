package com.hearthappy.processor.datahandler

import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSValueArgument
import com.hearthappy.processor.constant.Constant


fun <R : Any> List<KSValueArgument>.findArgsValue(paramName: String): R {
    for (arg in this) {
        if (arg.name?.asString() == paramName) {
            @Suppress("UNCHECKED_CAST")
            return arg.value as R
        }
    }
    throw IllegalArgumentException("No argument found with name: $paramName")
}


fun Sequence<KSAnnotation>.findSpecifiedAnt(vararg annotationNames: String) = this.find { annotationNames.contains(it.shortName.asString()) }
fun Sequence<KSAnnotation>.filterSpecifiedAnt(vararg annotationNames: String) = this.filter { annotationNames.contains(it.shortName.asString()) }

fun String.bindSuffix() = this.removePrefix("Bind")

fun String.privateType() = if (this == Constant.BIND_LIVE_DATA) Constant.MUTABLE_LIVEDATA else Constant.MUTABLE_STATE_FLOW

fun String.publicType() = if (this == Constant.BIND_LIVE_DATA) Constant.LIVEDATA else Constant.STATE_FLOW

fun String.privateImport() = if (this == Constant.BIND_LIVE_DATA) Constant.ANDROIDX_LIFECYCLE_PKG else Constant.STATE_FLOW_PKG

fun String.publicImport() = if (this == Constant.BIND_LIVE_DATA) Constant.ANDROIDX_LIFECYCLE_PKG else Constant.STATE_FLOW_PKG

fun String.privatePropertyName() = "_${this}"

fun String.className2PropertyName() = this.replaceFirstChar { it.lowercaseChar() }

fun String.rename(): String {
    return convertToCamelCase(this).plus("DataStore")
}

fun convertToCamelCase(input: String): String {
    return input.split("_")
        .joinToString("") { joinStr -> joinStr.replaceFirstChar { it.uppercaseChar() } }
        .replaceFirstChar { it.lowercaseChar() }
}

