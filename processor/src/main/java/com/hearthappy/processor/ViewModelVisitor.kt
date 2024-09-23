package com.hearthappy.processor

import com.google.devtools.ksp.isAbstract
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.hearthappy.processor.constant.AndroidTypeNames
import com.hearthappy.processor.constant.BindFunctionArgs
import com.hearthappy.processor.constant.Constant
import com.hearthappy.processor.constant.Constant.APP
import com.hearthappy.processor.constant.ViewModelAutomationArgs
import com.hearthappy.processor.datahandler.DataCheck
import com.hearthappy.processor.datahandler.bindSuffix
import com.hearthappy.processor.datahandler.className2PropertyName
import com.hearthappy.processor.datahandler.findSpecifiedAnt
import com.hearthappy.processor.exceptions.VMAAnalysisException
import com.hearthappy.processor.log.TAG_VMA
import com.hearthappy.processor.log.printVma
import com.hearthappy.processor.model.FunctionData
import com.hearthappy.processor.model.GenerateViewModelData
import com.hearthappy.processor.model.ViewModelData
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ksp.toTypeName

class ViewModelVisitor(
    private val logger: KSPLogger,
    private val generateData: GenerateViewModelData,
    private val index: Int
) : KSVisitorVoid() {
    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
        generateData.viewModelData.add(ViewModelData())
        val viewModelData = generateData.viewModelData.get(index = index)
        viewModelData.containingFile = classDeclaration.containingFile
        classDeclaration.apply {
            val apiClassName = simpleName.asString()
            if (this.isAbstract()) {
                parsingClassAndAnnotation(viewModelData, apiClassName)
                classDeclaration.getAllFunctions().forEach { it.accept(this@ViewModelVisitor, data) }
            } else throw VMAAnalysisException("$TAG_VMA: Please declare the ViewModelAutomation annotation on the interface,currently declare it on the <${apiClassName}> class")
        }


    }


    override fun visitFunctionDeclaration(function: KSFunctionDeclaration, data: Unit) {
        val viewModelData = generateData.viewModelData.get(index = index)
        val functionName = function.simpleName.asString()
        if (DataCheck.isFunction(functionName)) {
            FunctionData().apply {
                this.methodName = functionName
                parsingFunAnnotation(function, this, functionName, viewModelData)
                parsingFunParams(function, this, viewModelData)
                parsingFunReturnType(function, this, viewModelData)
                viewModelData.functionList.add(this)
            }
            logger.printVma(viewModelData.enabledLog, "function name: $functionName")
        }
        super.visitFunctionDeclaration(function, data)
    }

    /**
     * 解析类和注解
     * @receiver KSClassDeclaration
     * @param viewModelData ViewModelData
     */
    private fun KSClassDeclaration.parsingClassAndAnnotation(viewModelData: ViewModelData, apiClassName: String) {
        val apiPackage = packageName.asString()
        viewModelData.apply {
            annotations.findSpecifiedAnt(Constant.VIEW_MODEL_AUTOMATION)?.let {
                for (argument in it.arguments) {
                    when (argument.name?.asString()) {
                        ViewModelAutomationArgs.FILENAME    -> className = argument.value.toString()
                        ViewModelAutomationArgs.ENABLED_LOG -> enabledLog = argument.value as Boolean
                        ViewModelAutomationArgs.AGGREGATING -> aggregating = argument.value as Boolean
                    }
                }
                api = apiClassName.className2PropertyName()
                constructorParams.add(ParameterSpec(api, ClassName(apiPackage, apiClassName)))
                constructorParams.add(ParameterSpec(APP, AndroidTypeNames.Application))
                logger.printVma(enabledLog, "interface name:$apiClassName")
                logger.printVma(enabledLog, "class annotation:shortName:${it.shortName.asString()},annotationType:${it.annotationType},${it.arguments.toList()},fileName:${className}")
            }
        }
    }

    /**
     * 解析函数的注解
     * @param function KSFunctionDeclaration
     * @param functionData FunctionData
     * @param functionName String
     * @param viewModelData ViewModelData
     */
    private fun parsingFunAnnotation(function: KSFunctionDeclaration, functionData: FunctionData, functionName: String, viewModelData: ViewModelData) {
        //查找BindLiveData和BindStateFlow注解
        function.annotations.findSpecifiedAnt(Constant.BIND_LIVE_DATA, Constant.BIND_STATE_FLOW)?.let {
            for (argument in it.arguments) {
                when (argument.name?.asString()) {
                    BindFunctionArgs.METHOD_NAME   -> {
                        val methodName = argument.value as String
                        functionData.methodAliasName = methodName.isNotEmpty().takeIf { tif -> tif }?.run { methodName } ?: functionName
                    }

                    BindFunctionArgs.PROPERTY_NAME -> {
                        val propertyName = argument.value as String
                        functionData.propertyAliasName = propertyName.isNotEmpty().takeIf { tif -> tif }?.run { propertyName } ?: run { functionName.plus(it.shortName.asString().bindSuffix()) }
                    }
                }
            }
            functionData.annotationType = it.shortName.asString()
            logger.printVma(viewModelData.enabledLog, "function annotation--->name:${it.shortName.asString()},args:${it.arguments.toList()}")
        } ?: throw VMAAnalysisException("No BindLiveData or BindStateFlow annotation was found. Please declare the annotation on the <$functionName> function")
    }


    /**
     * 解析函数返回类型
     * @param function KSFunctionDeclaration
     * @param functionData FunctionData
     * @param viewModelData ViewModelData
     */
    private fun parsingFunReturnType(function: KSFunctionDeclaration, functionData: FunctionData, viewModelData: ViewModelData) {
        function.returnType?.resolve()?.apply {
            //KSType.toTypeName()扩展函数直接可获取复杂返回类型
            functionData.returnType = this.toTypeName()
            logger.printVma(viewModelData.enabledLog, "function returnType--->${functionData.returnType}")
        }
    }


    /**
     * 解析函数的参数列表
     * @param function KSFunctionDeclaration
     * @param functionData FunctionData
     * @param viewModelData ViewModelData
     */
    private fun parsingFunParams(function: KSFunctionDeclaration, functionData: FunctionData, viewModelData: ViewModelData) {
        function.parameters.map {
            val paramName = it.name?.asString() ?: throw VMAAnalysisException("paramName is null")
            logger.printVma(viewModelData.enabledLog, "function parameter--->name:$paramName,typeName:${it.type.toTypeName()}")
            ParameterSpec(paramName, it.type.toTypeName())
        }.also {
            functionData.parameterList.addAll(it)
        }
    }

}