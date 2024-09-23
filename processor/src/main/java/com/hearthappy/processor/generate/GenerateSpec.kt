package com.hearthappy.processor.generate

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.hearthappy.processor.constant.Constant
import com.hearthappy.processor.constant.Constant.APP
import com.hearthappy.processor.model.ViewModelData
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.writeTo

class GenerateSpec {

    /**
     * 生成Class
     * @param className String
     * @param constructorParameters List<ParameterSpec>
     * @param isAddConstructorProperty Boolean 是否添加到全局构造，提供内部使用
     * @param superClassName ClassName? 继承父类ClassName
     * @return TypeSpec.Builder
     */
    fun generateClass(className: String, constructorParameters: List<ParameterSpec> = listOf(), isAddConstructorProperty: Boolean = true, superClassName: ClassName? = null): TypeSpec.Builder {
        return TypeSpec.classBuilder(className).apply {
            if (constructorParameters.isNotEmpty()) { //创建构造参数
                primaryConstructor(FunSpec.constructorBuilder().addParameters(constructorParameters).build())

                if (isAddConstructorProperty) { //创建构造参数属性，（即：添加val ，提供内部引用）
                    for (cp in constructorParameters) addProperty(generatePropertySpec(cp.name, cp.type, cp.name, KModifier.PRIVATE))

                    for (cp in constructorParameters) if (cp.name == APP) addSuperclassConstructorParameter(cp.name)
                }

            }
            superClassName?.let { superclass(it) }
        }
    }

    /**
     * 生成属性并直接初始化
     * @param propertyName String 属性命名
     * @param propertyType TypeName 支持传入ParameterizedTypeName、ClassName。（即：泛型或一般类型）
     * @param initValue String 初始化值
     * @param modifier Array<out KModifier>
     * @return PropertySpec
     */
    fun generatePropertySpec(propertyName: String, propertyType: TypeName, initValue: String, vararg modifier: KModifier): PropertySpec {
        return PropertySpec.builder(propertyName, propertyType).initializer(initValue).addModifiers(*modifier).build()
    }


    /**
     * 生成委托属性
     * @param propertyName String
     * @param propertyType TypeName
     * @param delegateValue String
     * @param modifier Array<out KModifier>
     * @return PropertySpec
     */
    fun generateDelegatePropertySpec(propertyName: String, propertyType: TypeName, delegateValue: String, vararg modifier: KModifier): PropertySpec {
        return PropertySpec.builder(propertyName, propertyType).delegate("lazy{$delegateValue}").addModifiers(*modifier).build()
    }


    internal fun generateFileAndWrite(vma: ViewModelData, generateClass: TypeSpec.Builder, codeGenerator: CodeGenerator) { //创建文件
        FileSpec.builder(Constant.GENERATE_VIEWMODEL_PKG, vma.className)
            .addType(generateClass.build()).build().writeTo(codeGenerator, Dependencies(vma.aggregating, vma.containingFile!!))
    }
}

//fun generateFunctionSpec() {}