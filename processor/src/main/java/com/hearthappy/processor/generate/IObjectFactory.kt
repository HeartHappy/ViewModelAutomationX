package com.hearthappy.processor.generate

import com.google.devtools.ksp.processing.CodeGenerator
import com.squareup.kotlinpoet.TypeSpec
/**
 * @author ChenRui
 * ClassDescription：生成对象接口
 */
interface IObjectFactory {

    fun createFileSpec(): TypeSpec.Builder

    fun TypeSpec.Builder.generateProperty(preferences: MutableMap<String, String>)

    fun TypeSpec.Builder.generateAndWriteFile(codeGenerator: CodeGenerator)

}