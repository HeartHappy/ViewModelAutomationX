package com.hearthappy.processor.model

import com.google.devtools.ksp.symbol.KSFile
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeName
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.properties.Delegates

class ViewModelData {
    var className: String by Delegates.notNull()
    var api: String by Delegates.notNull()
    var constructorParams = mutableListOf<ParameterSpec>()
    var functionList = mutableListOf<FunctionData>()
    var enabledLog: Boolean = false
    var containingFile: KSFile? = null
    var aggregating: Boolean = false
    var imports = mutableListOf<ClassName>() //导包集合
    override fun toString(): String {
        return "ViewModelData(functionList=$functionList, className=$className,enabledLog=$enabledLog)"
    }
}