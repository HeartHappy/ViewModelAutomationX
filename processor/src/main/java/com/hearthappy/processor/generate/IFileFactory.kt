package com.hearthappy.processor.generate

import com.google.devtools.ksp.processing.CodeGenerator
import com.hearthappy.processor.model.DataStoreData
import com.squareup.kotlinpoet.FileSpec

interface IFileFactory {

    fun generateDataStoreFile(fileName: String): FileSpec.Builder

    fun FileSpec.Builder.generateProperty(gds: DataStoreData)

    fun FileSpec.Builder.generateAndWriteFile(dataStoreData: DataStoreData, codeGenerator: CodeGenerator)
}