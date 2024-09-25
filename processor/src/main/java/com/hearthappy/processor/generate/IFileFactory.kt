package com.hearthappy.processor.generate

import com.google.devtools.ksp.processing.CodeGenerator
import com.hearthappy.processor.model.GenerateDataStoreData
import com.hearthappy.processor.model.ViewModelData
import com.squareup.kotlinpoet.FileSpec

interface IFileFactory {

    fun generateDataStoreFile(): FileSpec.Builder

    fun FileSpec.Builder.generateProperty(gds: GenerateDataStoreData)

    fun FileSpec.Builder.generateAndWriteFile(codeGenerator: CodeGenerator)
}