package com.hearthappy.processor

import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.isAbstract
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.hearthappy.processor.constant.Constant
import com.hearthappy.processor.constant.Constant.APP
import com.hearthappy.processor.datahandler.DataCheck
import com.hearthappy.processor.datahandler.DataCheck.findAny
import com.hearthappy.processor.datahandler.bindSuffix
import com.hearthappy.processor.datahandler.className2PropertyName
import com.hearthappy.processor.datahandler.findArgsValue
import com.hearthappy.processor.datahandler.findSpecifiedAnt
import com.hearthappy.processor.exceptions.VMAAnalysisException
import com.hearthappy.processor.ext.AndroidTypeNames
import com.hearthappy.processor.ext.BindFunctionArgs
import com.hearthappy.processor.ext.DataStoreArgs
import com.hearthappy.processor.ext.DataStoreStorageArgs
import com.hearthappy.processor.ext.ViewModelAutomationArgs
import com.hearthappy.processor.log.TAG_VMA
import com.hearthappy.processor.log.printVma
import com.hearthappy.processor.model.FunctionData
import com.hearthappy.processor.model.GenerateViewModelData
import com.hearthappy.processor.model.StorageData
import com.hearthappy.processor.model.StorageList
import com.hearthappy.processor.model.ViewModelData
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ksp.toTypeName

class ViewModelVisitor(
    private val resolver: Resolver,
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
            logger.printVma(viewModelData.enabledLog, "function name: $functionName")
            FunctionData().apply {
                this.methodName = functionName
                parsingFunAnnotation(function, this, functionName, viewModelData)
                parsingFunParams(function, this, viewModelData)
                parsingFunReturnType(function, this, viewModelData)
                viewModelData.functionList.add(this)
            }
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
            functionData.returnType = toTypeName()
            logger.printVma(viewModelData.enabledLog, "function returnType--->${functionData.returnType}")

            parsingStorage(functionData, null, viewModelData.enabledLog)
        }
    }


    /**
     * 解析存储方式
     * @receiver KSType
     */
    private fun KSType.parsingStorage(functionData: FunctionData, parent: String?, enabledLog: Boolean) {
        val classDeclarationByName = resolver.getClassDeclarationByName(toTypeName().toString())
        classDeclarationByName?.let {
            val storageData = StorageData()
            it.annotations.findSpecifiedAnt(Constant.DATASTORE)?.arguments?.forEach {
                when (it.name?.asString()) {
                    DataStoreArgs.NAME -> storageData.name = it.value.toString()
                }
            }

            it.getAllProperties().forEach {
                parsingDataStoreStorage(it, parent, functionData, storageData, enabledLog)
                if (it.type.toTypeName().toString().findAny()) {
                    val prevParent = parent?.run { this.plus(".").plus(it.simpleName.asString()) } ?: it.simpleName.asString()
//                logger.printVma(true,"DataStore is Any:${it.simpleName.asString()},${it.type.toTypeName()},prevParent:${prevParent}")
                    it.type.resolve().parsingStorage(functionData, prevParent, enabledLog)
                }
            }
        }
    }


    /**
     * 解析响应数据需要存储的字段：DataStore存储
     * @param it KSPropertyDeclaration
     * @param parent String?
     * @param functionData FunctionData
     */
    private fun parsingDataStoreStorage(it: KSPropertyDeclaration, parent: String?, functionData: FunctionData, storageData: StorageData, enabledLog: Boolean) {
        it.annotations.findSpecifiedAnt(Constant.DATASTORE_STORAGE)?.let { findAnt ->
            //注解只适用基本数据类型，暂不支持对象、集合、数组
            if (it.type.toTypeName().toString().findAny()) {
                throw VMAAnalysisException("Annotations are only applicable to basic data types and do not currently support objects, collections, and arrays.property name:${it.simpleName.asString()},TypeName:<${it.type.toTypeName()}>")
            }

            val key = findAnt.arguments.findArgsValue<String>(DataStoreStorageArgs.KEY)
            val value = parent?.plus(".")?.plus(it.simpleName.asString()) ?: it.simpleName.asString() //获取message
            val typeName = it.type.toTypeName().toString()
            logger.printVma(enabledLog, "DataStoreStorage annotation--->key:$key,--->value:$value,--->type:$typeName,name:${functionData.storageData?.name}")
            storageData.storageList.add(StorageList(key, value, typeName))
            functionData.storageData = storageData

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