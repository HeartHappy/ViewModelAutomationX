package com.hearthappy.processor

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.hearthappy.processor.constant.Constant
import com.hearthappy.processor.datahandler.findSpecifiedAnt
import com.hearthappy.processor.datahandler.reConstName
import com.hearthappy.processor.ext.DataStoreArgs
import com.hearthappy.processor.log.printDataStore
import com.hearthappy.processor.model.DataStoreData
import com.hearthappy.processor.model.GenerateDataStoreData


/**
 * 解析DataStore和Write，用于生成DataStoreExt和PreferencesKeys文件
 * @property resolver Resolver
 * @property logger KSPLogger
 * @property generateData GenerateDataStoreData
 * @property storageList MutableMap<String, String>
 * @constructor
 */
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
            val findDataStoreAnt = classDeclaration.annotations.findSpecifiedAnt(Constant.DATASTORE)
            findDataStoreAnt?.let {
                it.arguments.forEach { argument ->
                    when (argument.name?.asString()) {
                        DataStoreArgs.NAME        -> name = argument.value.toString()
                        DataStoreArgs.ENABLED_LOG -> enabledLog = argument.value as Boolean
                        DataStoreArgs.AGGREGATING -> aggregating = argument.value as Boolean
                    }
                }
                generateData.dataStoreData.add(dataStoreData)
                logger.printDataStore(enabledLog, "dataStoreData:$dataStoreData")
            }
            classDeclaration.getAllProperties().forEach { it.accept(this@DataStoreVisitor, Unit) }
        }

        super.visitClassDeclaration(classDeclaration, data)
    }


    override fun visitPropertyDeclaration(property: KSPropertyDeclaration, data: Unit) {
        val argument = property.annotations.findSpecifiedAnt(Constant.DATA_WRITE)?.arguments?.first()
        argument?.let {
            generateData.dataStoreData[index].apply {
                val storageValue = it.value.toString()//msg
                val storageKey = storageValue.reConstName()//MSG
                this.storageMap[storageKey] = storageValue
            }
        }

        recursiveSearch(property, data)
        super.visitPropertyDeclaration(property, data)
    }

    private fun recursiveSearch(property: KSPropertyDeclaration, data: Unit) {
        // 检查属性类型
        val type = property.type.resolve()
        //如果属性是一个类，则继续递归访问该类的属性
        if (type.declaration is KSClassDeclaration) {
            // 检查是否为集合类型（如 List、Set 等）
            if (type.declaration.qualifiedName?.asString() in listOf("kotlin.collections.List", "kotlin.collections.Set", "kotlin.collections.Map")) {
                // 处理泛型参数
                type.arguments.forEach { typeArgument ->
                    val argumentType = typeArgument.type?.resolve()
                    argumentType?.let {
                        if (it.declaration is KSClassDeclaration) {
                            // 如果泛型参数是一个类，则递归访问
                            (it.declaration as KSClassDeclaration).accept(this, data)
                        }
                    }
                }
            } else {
                (type.declaration as KSClassDeclaration).accept(this, data)
            }
        }
    }
}