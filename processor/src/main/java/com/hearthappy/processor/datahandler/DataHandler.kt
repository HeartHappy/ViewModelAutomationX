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

/**
 * 转换成全大写和下划线组合名称
 * @receiver String
 * @return String
 */
fun String.reConstName(): String {
    // 使用正则表达式将小写字母和大写字母分开
    return replace(Regex("([a-z])([A-Z])"), "$1_$2")
        // 将整个字符串转换为大写
        .uppercase()
}

/**
 * 转换成去下划线和驼峰命名跪着
 * @receiver String
 * @return String
 */
fun String.rename(): String {
    return convertToCamelCase(this).plus("DataStore")
}

fun String.renameIt(): String {
    return this.substring(0, if (this.length > 3) 3 else 1)
}


/**
 * 转换成文件名格式。例如：UserInfoExt
 * @receiver String
 * @return String
 */
fun String.reFileName(): String {
    return this.split("_")
        .joinToString("") { it.replaceFirstChar { rfc -> rfc.uppercaseChar() } }
        .replaceFirstChar { it.uppercase() }.plus("Ext")
}

fun String.rePreferencesKeysName(): String {
    return this.split("_")
        .joinToString("") { it.replaceFirstChar { rfc -> rfc.uppercaseChar() } }
        .replaceFirstChar { it.uppercase() }.plus("Keys")
}

fun convertToCamelCase(input: String): String {
    return input.split("_")
        .joinToString("") { joinStr -> joinStr.replaceFirstChar { it.uppercaseChar() } }
        .replaceFirstChar { it.lowercaseChar() }
}

