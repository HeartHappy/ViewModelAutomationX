package com.hearthappy.processor.generate.impl

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.hearthappy.processor.constant.Constant
import com.hearthappy.processor.constant.Constant.FLOW_RESULT
import com.hearthappy.processor.constant.Constant.LIVE_DATA_RESULT
import com.hearthappy.processor.constant.LifecyclesTypeNames
import com.hearthappy.processor.constant.VMANetworkTypeNames
import com.hearthappy.processor.datahandler.privatePropertyName
import com.hearthappy.processor.generate.GenerateSpec
import com.hearthappy.processor.generate.IGenerateFactory
import com.hearthappy.processor.log.printVma
import com.hearthappy.processor.model.FunctionData
import com.hearthappy.processor.model.ViewModelData
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec

class GenerateFactory(private val logger: KSPLogger) : IGenerateFactory {

    private val generateSpec by lazy { GenerateSpec() }

    override fun generateViewModel(vma: ViewModelData): TypeSpec.Builder {
        logger.printVma(vma.enabledLog, "Generating class:name:${vma.className},constructorParams:${vma.constructorParams.joinToString { it.name }}")
        return generateSpec.generateClass(vma.className, vma.constructorParams, superClassName = LifecyclesTypeNames.AndroidViewModel)
    }

    override fun TypeSpec.Builder.generateProperty(vma: ViewModelData) {
        vma.functionList.forEach {
            val returnType = it.returnType?.run { it.returnParentType.parameterizedBy(this) } ?: it.returnParentType
            logger.printVma(vma.enabledLog, "Generating property:name:${it.propertyAliasName},annotationType:${it.annotationType},returnType: $returnType")
            when (it.annotationType) {
                Constant.BIND_LIVE_DATA -> generatePropertyByLiveData(it.propertyAliasName, returnType)
                else                    -> generatePropertyByStateFlow(it.propertyAliasName, returnType)
            }
        }
    }


    override fun TypeSpec.Builder.generateMethod(vma: ViewModelData) {
        vma.functionList.forEach {
            val returnType = it.returnType?.run { it.returnParentType.parameterizedBy(this) } ?: it.returnParentType
            val function = FunSpec.builder(it.methodAliasName).apply {
                this.addParameters(it.parameterList)
                when (it.annotationType) {
                    Constant.BIND_LIVE_DATA -> generateFunctionContent(returnType, it.propertyAliasName, vma.api, it, LIVE_DATA_RESULT)
                    else                    -> generateFunctionContent(returnType, it.propertyAliasName, vma.api, it, FLOW_RESULT)
                }
                logger.printVma(vma.enabledLog, "Generating function:name:${it.methodName},params:${it.parameterList.joinToString { jts -> jts.name }}")
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
        addProperty(generateSpec.generateDelegatePropertySpec(propertyName.privatePropertyName(), LifecyclesTypeNames.MutableLiveData.parameterizedBy(VMANetworkTypeNames.LiveDataResult.parameterizedBy(resultClassName)), "${Constant.MUTABLE_LIVEDATA}()", KModifier.PRIVATE))
        //Create a public property
        addProperty(generateSpec.generatePropertySpec(propertyName, LifecyclesTypeNames.LiveData.parameterizedBy(VMANetworkTypeNames.LiveDataResult.parameterizedBy(resultClassName)), initValue = propertyName.privatePropertyName()))
    }

    private fun TypeSpec.Builder.generatePropertyByStateFlow(propertyName: String, resultClassName: TypeName) {
        //Create a private property
        addProperty(generateSpec.generateDelegatePropertySpec(propertyName.privatePropertyName(), LifecyclesTypeNames.MutableStateFlow.parameterizedBy(VMANetworkTypeNames.FlowResult.parameterizedBy(resultClassName)), "${Constant.MUTABLE_STATE_FLOW}(${FLOW_RESULT}.Default)", KModifier.PRIVATE))
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
    private fun FunSpec.Builder.generateFunctionContent(resultClassName: TypeName, propertyName: String, api: String, functionData: FunctionData, resultType: String) {
        addStatement("%T<%T>(io = {", VMANetworkTypeNames.RequestScope, resultClassName)
        if (resultType == FLOW_RESULT) addStatement("%L.value = %L.Loading", propertyName.privatePropertyName(), resultType)
        addStatement("%L.%L(%L)", api, functionData.methodName, functionData.parameterList.joinToString { it.name })
        addStatement("}, onSucceed = {")
        addStatement("%L.value = %L.Succeed(it)", propertyName.privatePropertyName(), resultType)
        addStatement("}, onThrowable = {")
        addStatement("%L.value =%L.Throwable(it)", propertyName.privatePropertyName(), resultType)
        addStatement("})")
    }

}