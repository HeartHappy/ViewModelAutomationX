package com.hearthappy.processor.ext

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSTypeParameter
import com.hearthappy.processor.exceptions.VMAAnalysisException


/**
 * 获取类的所有属性
 * @receiver KSDeclaration
 * @return Sequence<KSPropertyDeclaration>
 */
fun KSDeclaration.getAllClassProperty(): Sequence<KSPropertyDeclaration> {
    return when (this) {
        is KSClassDeclaration -> getAllProperties()

        else -> throw VMAAnalysisException("Class <${simpleName.asString()}> conversion exception ")
    }
}

/**
 * 获取类的泛型属性
 * @receiver KSDeclaration
 * @return KSPropertyDeclaration?
 */
fun KSDeclaration.getGenericProperty(): KSPropertyDeclaration? {
    val allClassProperty = getAllClassProperty()
    return allClassProperty.find { it.type.resolve().declaration is KSTypeParameter }
}