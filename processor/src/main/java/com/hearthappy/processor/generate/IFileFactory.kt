package com.hearthappy.processor.generate

import com.google.devtools.ksp.processing.CodeGenerator
import com.hearthappy.processor.model.DataStoreData
import com.squareup.kotlinpoet.FileSpec

/**
 * @author ChenRui
 * ClassDescription：DataStore工厂
 */
interface IFileFactory {

    fun generateDataStoreFile(fileName: String): FileSpec.Builder

    fun FileSpec.Builder.generateProperty(gds: DataStoreData)

    fun FileSpec.Builder.generateAndWriteFile(dataStoreData: DataStoreData, codeGenerator: CodeGenerator)
}