package com.hearthappy.processor.model

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeName
import kotlin.properties.Delegates

class FunctionData {
    var methodName: String by Delegates.notNull()
    var methodAliasName: String by Delegates.notNull()
    var propertyAliasName: String by Delegates.notNull()
    val parameterList: MutableList<ParameterSpec> = mutableListOf()
    var returnParentType: ClassName by Delegates.notNull()
    var returnType: ClassName? = null
    var annotationType: String by Delegates.notNull()


    override fun toString(): String {
        return "FunctionData(methodName='$methodName',methodNameAlias:$methodAliasName, parameterList=$parameterList, returnParentType=$returnParentType,returnType:$returnType, annotationType='$annotationType')"
    }

}
