package com.hearthappy.processor

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.hearthappy.processor.constant.Constant
import com.hearthappy.processor.datahandler.findSpecifiedAnt
import com.hearthappy.processor.ext.DataStoreArgs
import com.hearthappy.processor.ext.DataStoreTypeNames
import com.hearthappy.processor.model.DataStoreData
import com.hearthappy.processor.model.GenerateDataStoreData
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy

class DataStoreVisitor(
    private val resolver: Resolver,
    private val logger: KSPLogger,
    private val generateData: GenerateDataStoreData,
    private val index: Int
) : KSVisitorVoid() {

    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
        val dataStoreData = DataStoreData()

        dataStoreData.apply {
            this.containingFile = classDeclaration.containingFile
            classDeclaration.annotations.findSpecifiedAnt(Constant.DATASTORE)?.arguments?.forEach {
                when (it.name?.asString()) {
                    DataStoreArgs.NAME          -> name = it.value.toString()
                    DataStoreArgs.ENABLED_LOG   -> enabledLog = it.value as Boolean
                    DataStoreArgs.AGGREGATING   -> aggregating = it.value as Boolean
                }
            }
        }
        generateData.dataStoreData.add(dataStoreData)
        super.visitClassDeclaration(classDeclaration, data)
    }

    override fun visitPropertyDeclaration(property: KSPropertyDeclaration, data: Unit) {
        super.visitPropertyDeclaration(property, data)
    }
}