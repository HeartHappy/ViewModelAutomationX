package com.hearthappy.processor.generate.impl

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.hearthappy.processor.constant.Constant
import com.hearthappy.processor.ext.KotlinTypeNames
import com.hearthappy.processor.generate.IObjectFactory
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.writeTo

class GenerateObjectFileImpl : IObjectFactory {
    private val className: String = "PreferencesKeys"
    override fun createFileSpec(): TypeSpec.Builder {
        return TypeSpec.objectBuilder(className)
    }

    override fun TypeSpec.Builder.generateProperty(preferences: MutableMap<String, String>) {
        preferences.forEach { (key, value) ->
            addProperty(PropertySpec.builder(key, KotlinTypeNames.String, KModifier.PUBLIC).initializer("%S", value).build())
        }
    }

    override fun TypeSpec.Builder.generateAndWriteFile( codeGenerator: CodeGenerator) {
        FileSpec.builder(Constant.GENERATE_DATASTORE_PKG, className)
            .addType(this.build()).build().writeTo(codeGenerator, Dependencies(true))
    }

}