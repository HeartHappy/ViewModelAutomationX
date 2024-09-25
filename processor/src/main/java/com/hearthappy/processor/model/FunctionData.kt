package com.hearthappy.processor.model

import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeName
import kotlin.properties.Delegates

class FunctionData {
    var methodName: String by Delegates.notNull()
    var methodAliasName: String by Delegates.notNull()
    var propertyAliasName: String by Delegates.notNull()
    val parameterList: MutableList<ParameterSpec> = mutableListOf()

    //    var returnParentType: TypeName by Delegates.notNull()
    var returnType: TypeName by Delegates.notNull()
    var annotationType: String by Delegates.notNull()
    var storageData: StorageData? = null

    override fun toString(): String {
        return "FunctionData(methodName='$methodName', methodAliasName='$methodAliasName', propertyAliasName='$propertyAliasName', parameterList=$parameterList, returnType=$returnType, annotationType='$annotationType', dataStoreList=$storageData)"
    }


}
