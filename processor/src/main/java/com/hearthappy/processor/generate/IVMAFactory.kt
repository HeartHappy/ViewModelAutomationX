package com.hearthappy.processor.generate

import com.google.devtools.ksp.processing.CodeGenerator
import com.hearthappy.processor.model.ViewModelData
import com.squareup.kotlinpoet.TypeSpec


interface IVMAFactory {

    fun generateViewModel(vma: ViewModelData): TypeSpec.Builder

    fun TypeSpec.Builder.generateProperty(vma: ViewModelData)

    fun TypeSpec.Builder.generateMethod(vma: ViewModelData)

    fun TypeSpec.Builder.generateAndWriteFile(vma: ViewModelData, codeGenerator: CodeGenerator)

}

