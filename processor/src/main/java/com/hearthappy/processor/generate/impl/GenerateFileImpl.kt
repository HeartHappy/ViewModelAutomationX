package com.hearthappy.processor.generate.impl

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.hearthappy.processor.constant.Constant
import com.hearthappy.processor.datahandler.rename
import com.hearthappy.processor.ext.AndroidTypeNames
import com.hearthappy.processor.ext.DataStoreTypeNames
import com.hearthappy.processor.generate.IFileFactory
import com.hearthappy.processor.model.DataStoreData
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.ksp.writeTo

class GenerateFileImpl : IFileFactory {

    override fun generateDataStoreFile(fileName: String): FileSpec.Builder {
        return FileSpec.builder(Constant.GENERATE_DATASTORE_PKG, fileName)
    }

    override fun FileSpec.Builder.generateProperty(gds: DataStoreData) {
        addProperty(PropertySpec.builder(gds.name.rename(), gds.propertyType).receiver(AndroidTypeNames.Context).delegate("%T(name = %S)", DataStoreTypeNames.DataStorePreferences, gds.name).addModifiers(KModifier.PUBLIC).build())
    }

    override fun FileSpec.Builder.generateAndWriteFile(dataStoreData: DataStoreData, codeGenerator: CodeGenerator) {
        build().writeTo(codeGenerator, Dependencies(false, dataStoreData.containingFile!!))
    }
}