package com.hearthappy.processor.generate.impl

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.hearthappy.processor.constant.Constant
import com.hearthappy.processor.constant.Constant.FLOW_RESULT
import com.hearthappy.processor.constant.Constant.INDENTATION
import com.hearthappy.processor.constant.Constant.LIVE_DATA_RESULT
import com.hearthappy.processor.datahandler.privatePropertyName
import com.hearthappy.processor.datahandler.reConstName
import com.hearthappy.processor.datahandler.rePreferencesKeysName
import com.hearthappy.processor.datahandler.rename
import com.hearthappy.processor.datahandler.replaceLastQuestionMark
import com.hearthappy.processor.ext.DataStoreTypeNames
import com.hearthappy.processor.ext.LifecyclesTypeNames
import com.hearthappy.processor.ext.VMANetworkTypeNames
import com.hearthappy.processor.ext.defaultValue
import com.hearthappy.processor.ext.string2preferenceType
import com.hearthappy.processor.generate.GenerateSpec
import com.hearthappy.processor.generate.IVMAFactory
import com.hearthappy.processor.log.printVma
import com.hearthappy.processor.model.FunctionData
import com.hearthappy.processor.model.StorageData
import com.hearthappy.processor.model.ViewModelData
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec

/**
 * Created Date: 2024/2/8
 * @author ChenRui
 * ClassDescription： 生成VMA实体类
 */
class GenerateVMAImpl(private val logger: KSPLogger) : IVMAFactory {

    private val generateSpec by lazy { GenerateSpec() }

    override fun generateViewModel(vma: ViewModelData): TypeSpec.Builder {
        logger.printVma(vma.enabledLog, "Generating class:name:${vma.className},constructorParams:${vma.constructorParams.joinToString { it.name }}")
        return generateSpec.generateClass(vma.className, vma.constructorParams, superClassName = LifecyclesTypeNames.AndroidViewModel)
    }

    override fun TypeSpec.Builder.generateProperty(vma: ViewModelData) {
        vma.functionList.forEach {
//            val returnType = it.returnType?.run { it.returnParentType.parameterizedBy(this) } ?: it.returnParentType
            logger.printVma(vma.enabledLog, "Generating property--->name:${it.propertyAliasName},annotationType:${it.annotationType},returnType: ${it.returnType}")
            when (it.annotationType) {
                Constant.BIND_LIVE_DATA -> generatePropertyByLiveData(it.propertyAliasName, it.returnType)
                else -> generatePropertyByStateFlow(it.propertyAliasName, it.returnType)
            }
        }
    }


    override fun TypeSpec.Builder.generateMethod(vma: ViewModelData) {
        vma.functionList.forEach {
//            val returnType = it.returnType?.run { it.returnParentType.parameterizedBy(this) } ?: it.returnParentType
            val function = FunSpec.builder(it.methodAliasName).apply {
                this.addParameters(it.parameterList)
                when (it.annotationType) {
                    Constant.BIND_LIVE_DATA -> generateFunctionContent(vma, it, LIVE_DATA_RESULT)
                    else -> generateFunctionContent(vma, it, FLOW_RESULT)
                }
                logger.printVma(vma.enabledLog, "Generating function--->name:${it.methodName},params:${it.parameterList.joinToString { jts -> jts.name }}")
            }
            addFunction(function.build())
        }
    }

    override fun TypeSpec.Builder.generateAndWriteFile(vma: ViewModelData, codeGenerator: CodeGenerator) {
        generateSpec.generateFileAndWrite(vma, this, codeGenerator)
        logger.printVma(vma.enabledLog, "Create a ${vma.className} file and write the class to the file")
    }

    private fun TypeSpec.Builder.generatePropertyByLiveData(propertyName: String, resultClassName: TypeName) {
        //Create a private property
        addProperty(generateSpec.generateDelegatePropertySpec(propertyName = propertyName.privatePropertyName(), receiverType = null, propertyType = LifecyclesTypeNames.MutableLiveData.parameterizedBy(VMANetworkTypeNames.LiveDataResult.parameterizedBy(resultClassName)), delegateValue = "${Constant.MUTABLE_LIVEDATA}()", KModifier.PRIVATE))
        //Create a public property
        addProperty(generateSpec.generatePropertySpec(propertyName, LifecyclesTypeNames.LiveData.parameterizedBy(VMANetworkTypeNames.LiveDataResult.parameterizedBy(resultClassName)), initValue = propertyName.privatePropertyName()))
    }

    private fun TypeSpec.Builder.generatePropertyByStateFlow(propertyName: String, resultClassName: TypeName) {
        //Create a private property
        addProperty(generateSpec.generateDelegatePropertySpec(propertyName = propertyName.privatePropertyName(), receiverType = null, propertyType = LifecyclesTypeNames.MutableStateFlow.parameterizedBy(VMANetworkTypeNames.FlowResult.parameterizedBy(resultClassName)), delegateValue = "${Constant.MUTABLE_STATE_FLOW}(${FLOW_RESULT}.Default)", KModifier.PRIVATE))
        //Create a public property
        addProperty(generateSpec.generatePropertySpec(propertyName, LifecyclesTypeNames.StateFlow.parameterizedBy(VMANetworkTypeNames.FlowResult.parameterizedBy(resultClassName)), initValue = propertyName.privatePropertyName()))
    }


    /**
     * addStatement()占位符
     * %S：表示一个字符串。
     * %T：表示一个类型。
     * %L：表示一个字面量（literal）。
     * %N：表示一个标识符（identifier）。
     */
    private fun FunSpec.Builder.generateFunctionContent(vma: ViewModelData, fd: FunctionData, resultType: String) {
        addStatement("%T<%T>(io = {", VMANetworkTypeNames.RequestScope, fd.returnType)
        if (resultType == FLOW_RESULT) addStatement("%L%L.value = %L.Loading", INDENTATION, fd.propertyAliasName.privatePropertyName(), resultType)
        addStatement("%L%L.%L(%L)", INDENTATION, vma.api, fd.methodName, fd.parameterList.joinToString { it.name })
        addStatement("}, onSucceed = {")
        addStatement("%L%L.value = %L.Succeed(it)", INDENTATION, fd.propertyAliasName.privatePropertyName(), resultType)
        addStatement("}, onThrowable = {")
        addStatement("%L%L.value = %L.Throwable(it)", INDENTATION, fd.propertyAliasName.privatePropertyName(), resultType)
        generateOnDataStore(fd, vma)
        addStatement("})")
    }

    private fun FunSpec.Builder.generateOnDataStore(fd: FunctionData, vma: ViewModelData) {
        fd.storageList?.apply {
            this.name?.let { name ->
                val dataStorePropertyName = name.rename()
                val dataStorePreferencesKeys = name.rePreferencesKeysName()
                if (this.storageData.isNotEmpty()) {
                    val preferences = "preferences"
                    vma.imports.add(ClassName(Constant.GENERATE_DATASTORE_PKG, dataStorePropertyName))
                    vma.imports.add(ClassName(Constant.GENERATE_DATASTORE_PKG, dataStorePreferencesKeys))
                    vma.imports.add(DataStoreTypeNames.DataStoreEdit)
//                    vma.imports.add(DataStoreTypeNames.DataStorePreferencesKeys)
                    addStatement("}, onDataStore = {")
                    addStatement("%L%L.%L.edit { %L->", INDENTATION, Constant.APP, dataStorePropertyName, preferences)
                    handlerNullProperties(storageData, preferences, vma, name)
                    addStatement("%L}", INDENTATION)
                }
            }
        }
    }

    /**
     * 处理 preferences[intPreferencesKey(UserImageTableKeys.CODE)] = it.code 存储
     * @receiver FunSpec.Builder
     * @param storageData MutableList<StorageList>
     * @param preferences String
     * @param vma ViewModelData
     * @param name String
     */
    private fun FunSpec.Builder.handlerNullProperties(storageData: MutableList<StorageData>, preferences: String, vma: ViewModelData, name: String) {
        val filter = storageData.filter { it.actionValue.contains(".") && !it.actionValue.contains("?") }
        if (filter.isNotEmpty()) filter.first().actionValue.split(".").apply { addStatement("%L%Lit.${this[0]}?:return@edit", INDENTATION, INDENTATION) }
        storageData.forEach {
            logger.printVma(true, "storageData:${it.actionValue}，key:${it.key},type:${it.type}")
            if (it.actionValue.contains(".")) if (it.actionValue.contains("?")) {//为空的处理
                addStatement("%L%L%L[%L(%L.%L)] = it.%L?:%L", INDENTATION, INDENTATION, preferences, it.type.string2preferenceType(vma.imports), name.rePreferencesKeysName(), it.key.reConstName(), it.actionValue.replaceLastQuestionMark(), it.type.defaultValue())
            } else {
                addStatement("%L%L%L[%L(%L.%L)] = it.%L", INDENTATION, INDENTATION, preferences, it.type.string2preferenceType(vma.imports), name.rePreferencesKeysName(), it.key.reConstName(), it.actionValue)
            }
        }
    }
}