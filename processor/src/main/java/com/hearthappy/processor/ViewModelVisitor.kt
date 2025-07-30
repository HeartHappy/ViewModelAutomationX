package com.hearthappy.processor

import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.isAbstract
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.hearthappy.processor.constant.Constant
import com.hearthappy.processor.constant.Constant.APP
import com.hearthappy.processor.datahandler.DataCheck
import com.hearthappy.processor.datahandler.DataCheck.getGenericType
import com.hearthappy.processor.datahandler.DataCheck.hasGeneric
import com.hearthappy.processor.datahandler.DataCheck.isBasicDataTypes
import com.hearthappy.processor.datahandler.className2PropertyName
import com.hearthappy.processor.datahandler.convertFirstChar
import com.hearthappy.processor.datahandler.convertToPrefix
import com.hearthappy.processor.datahandler.findArgsValue
import com.hearthappy.processor.datahandler.findSpecifiedAnt
import com.hearthappy.processor.datahandler.reConstName
import com.hearthappy.processor.exceptions.VMAAnalysisException
import com.hearthappy.processor.ext.AndroidTypeNames
import com.hearthappy.processor.ext.BindFunctionArgs
import com.hearthappy.processor.ext.DataStoreArgs
import com.hearthappy.processor.ext.ViewModelAutomationArgs
import com.hearthappy.processor.ext.getGenericProperty
import com.hearthappy.processor.log.KSPLog
import com.hearthappy.processor.model.FunctionData
import com.hearthappy.processor.model.StorageData
import com.hearthappy.processor.model.StorageList
import com.hearthappy.processor.model.ViewModelData
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.ksp.toTypeName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

/**
 * @author ChenRui
 * ClassDescription： 解析ViewModel相关数据
 */

class ViewModelVisitor(private val channel: Channel<ViewModelData>, private val coroutineScope: CoroutineScope, private val resolver: Resolver) : KSVisitorVoid() {
    private val viewModelData = ViewModelData()

    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
        viewModelData.containingFile = classDeclaration.containingFile
        classDeclaration.apply {
            val apiClassName = simpleName.asString()
            if (this.isAbstract()) {
                parsingClassAndAnnotation(viewModelData, apiClassName)
                classDeclaration.getAllFunctions().forEach { it.accept(this@ViewModelVisitor, data) }
                coroutineScope.launch { channel.send(viewModelData) }
            } else throw VMAAnalysisException("VMA: Please declare the ViewModelAutomation annotation on the interface,currently declare it on the <${apiClassName}> class")
        }
    }


    override fun visitFunctionDeclaration(function: KSFunctionDeclaration, data: Unit) {
        val functionName = function.simpleName.asString()
        if (DataCheck.isFunction(functionName)) {
            KSPLog.printVma(viewModelData.enabledLog, "function name: $functionName")
            FunctionData().apply {
                this.methodName = functionName
                parsingFunAnnotation(function, this, functionName, viewModelData)
                parsingFunParams(function, this, viewModelData)
                parsingFunReturnType(function, this, viewModelData)
                viewModelData.functionList.add(this@apply)
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
                        ViewModelAutomationArgs.FILENAME -> className = argument.value.toString()
                        ViewModelAutomationArgs.ENABLED_LOG -> enabledLog = argument.value as Boolean
                        ViewModelAutomationArgs.AGGREGATING -> aggregating = argument.value as Boolean
                    }
                }
                api = apiClassName.className2PropertyName()
                constructorParams.add(ParameterSpec(api, ClassName(apiPackage, apiClassName)))
                constructorParams.add(ParameterSpec(APP, AndroidTypeNames.Application))
                KSPLog.printVma(enabledLog, "interface name:$apiClassName")
                KSPLog.printVma(enabledLog, "class annotation:shortName:${it.shortName.asString()},annotationType:${it.annotationType},${it.arguments.toList()},fileName:${className}")
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
    private fun parsingFunAnnotation(function: KSFunctionDeclaration, functionData: FunctionData, functionName: String, viewModelData: ViewModelData) { //查找BindLiveData和BindStateFlow注解
        function.annotations.findSpecifiedAnt(Constant.BIND_LIVE_DATA, Constant.BIND_STATE_FLOW)?.let {
            for (argument in it.arguments) {
                when (argument.name?.asString()) {
                    BindFunctionArgs.METHOD_NAME -> {
                        val methodName = argument.value as String
                        functionData.methodAliasName = methodName.isNotEmpty().takeIf { tif -> tif }?.run { methodName } ?: functionName
                    }

                    BindFunctionArgs.PROPERTY_NAME -> {
                        val propertyName = argument.value as String
                        functionData.propertyAliasName = propertyName.isNotEmpty().takeIf { tif -> tif }?.run { propertyName } ?: run { it.shortName.asString().convertToPrefix().plus(functionName.convertFirstChar()) }
                    }
                }
            }
            functionData.annotationType = it.shortName.asString()
            KSPLog.printVma(viewModelData.enabledLog, "function annotation--->name:${it.shortName.asString()},args:${it.arguments.toList()}")
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
            KSPLog.printVma(viewModelData.enabledLog, "function returnType--->${functionData.returnType}")
            var typeName = toTypeName().toString()
            var parent: String? = null
            if (typeName.hasGeneric()) {
                val genericProperty = declaration.getGenericProperty()
                parent = genericProperty?.simpleName?.asString()
                typeName = typeName.getGenericType()
            }

            val classDeclarationByName = resolver.getClassDeclarationByName(typeName)
            classDeclarationByName?.let {
                val storageList = StorageList()
                val findArgsValue = it.annotations.findSpecifiedAnt(Constant.DATASTORE)?.arguments?.findArgsValue<String>(DataStoreArgs.NAME)
                storageList.name = findArgsValue //递归查找DataWrite注解
                storageList.genericT = parent
                findDataWrite(it, functionData, storageList, parent, null, viewModelData.enabledLog)
            }
        }
    }

    private fun findDataWrite(ksClassDeclaration: KSClassDeclaration, functionData: FunctionData, storageList: StorageList, parent: String?, parentTypeName: TypeName?, enabledLog: Boolean) {
        ksClassDeclaration.getAllProperties().forEach { property ->
            val propertyName = property.simpleName.asString()
            val typeName = property.type.toTypeName() //如果该属性包含注解，解析并存储
            val argument = property.annotations.findSpecifiedAnt(Constant.DATA_WRITE)?.arguments?.first() //存储调用值
            val storageValue = getStorageValue(parentTypeName, parent, propertyName)
            argument?.let {
                storageDataVerification(parentTypeName, property) //Write--->:storageKey:id,storageKeyRename:ID,propertyName:id,dataStorePropertyName:user_info,typename:kotlin.Int,storageValue:result.list.id
                val storageKey = it.value.toString()
                val storageKeyRename = storageKey.reConstName()
                KSPLog.printVma(enabledLog, "Write annotation--->storageKey:${storageKey}, " + "storageValue:${storageValue}, " + //it.result.total.id
                        "storageKeyRename:${storageKeyRename}, " + //PreferencesKey
                        "propertyName:${propertyName}, " + "dataStorePropertyName:${storageList.name}, " + //Context.dataStore扩展属性名
                        "typename:${typeName}, " //stringPreferencesKey
                ) //最后一个如果是空则添加后缀?
                storageList.storageData.add(StorageData(storageKey, if (typeName.toString().endsWith("?")) storageValue.plus("?") else storageValue, typeName.toString()))
            }
            val objectRelationArgument = property.annotations.findSpecifiedAnt(Constant.OBJECT_RELATION)
            val type = property.type.resolve()
            if (type.declaration is KSClassDeclaration && objectRelationArgument != null) {
                findDataWrite((type.declaration as KSClassDeclaration), functionData, storageList, storageValue, type.toTypeName(), enabledLog)
            } //            recursiveSearch(property, functionData, storageList, storageValue, enabledLog)
        }
        functionData.storageList = storageList
    }

    private fun getStorageValue(parentTypeName: TypeName?, parent: String?, propertyName: String): String {

        return if (parentTypeName.toString().endsWith("?")) {
            parent?.run { this.plus("?.").plus(propertyName) } ?: propertyName
        } else {
            parent?.run { this.plus(".").plus(propertyName) } ?: propertyName
        }
    }

    /**
     * 存储数据校验
     * @param parentTypeName TypeName?
     * @param property KSPropertyDeclaration
     */
    private fun storageDataVerification(parentTypeName: TypeName?, property: KSPropertyDeclaration) { //检查是否为集合类型（如 List、Set 等）
        if (DataCheck.isCollectionType(parentTypeName.toString())) {
            throw VMAAnalysisException("The annotation parameter Key must be unique and cannot be declared in a collection type.property name:<${property.simpleName.asString()}>,TypeName:<${property.type.toTypeName()}>,parentTypeName:<${parentTypeName}>")
        } //注解只适用基本数据类型，暂不支持对象、集合、数组
        if (!property.type.toTypeName().toString().isBasicDataTypes()) {
            throw VMAAnalysisException("Annotations are only applicable to basic data types and do not currently support objects, collections, and arrays.property name:<${property.simpleName.asString()}>,TypeName:<${property.type.toTypeName()}>")
        }
    }


    private fun recursiveSearch(property: KSPropertyDeclaration, functionData: FunctionData, storageList: StorageList, previousParent: String, enabledLog: Boolean) { // 检查属性类型
        val type = property.type.resolve() //如果属性是一个类，则继续递归访问该类的属性
        if (type.declaration is KSClassDeclaration) { // 检查是否为集合类型（如 List、Set 等）
            if (type.declaration.qualifiedName?.asString() in listOf("kotlin.collections.List", "kotlin.collections.Set", "kotlin.collections.Map")) { // 处理泛型参数
                type.arguments.forEach { typeArgument ->
                    val argumentType = typeArgument.type?.resolve()
                    argumentType?.let {
                        if (it.declaration is KSClassDeclaration) { // 如果泛型参数是一个类，则递归访问
                            findDataWrite((it.declaration as KSClassDeclaration), functionData, storageList, previousParent, type.toTypeName(), enabledLog)
                        }
                    }
                }
            } else {
                findDataWrite((type.declaration as KSClassDeclaration), functionData, storageList, previousParent, type.toTypeName(), enabledLog)
            }
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
            KSPLog.printVma(viewModelData.enabledLog, "function parameter--->name:$paramName,typeName:${it.type.toTypeName()}")
            ParameterSpec(paramName, it.type.toTypeName())
        }.also {
            functionData.parameterList.addAll(it)
        }
    }

}