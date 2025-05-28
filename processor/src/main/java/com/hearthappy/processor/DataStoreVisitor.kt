package com.hearthappy.processor

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.hearthappy.processor.constant.Constant
import com.hearthappy.processor.datahandler.findSpecifiedAnt
import com.hearthappy.processor.datahandler.reConstName
import com.hearthappy.processor.ext.DataStoreArgs
import com.hearthappy.processor.model.DataStoreData
import com.hearthappy.processor.model.GenerateDataStoreData

/**
 * @author ChenRui
 * ClassDescription： 解析DataStore和Write，用于生成DataStoreExt和PreferencesKeys文件
 *
 * @property resolver Resolver
 * @property generateData GenerateDataStoreData
 * @constructor
 */
class DataStoreVisitor(private val resolver: Resolver, private val generateData: GenerateDataStoreData, private val index: Int) : KSVisitorVoid() {

    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
        val dataStoreData = DataStoreData()
        dataStoreData.apply {
            this.containingFile = classDeclaration.containingFile
            val findDataStoreAnt = classDeclaration.annotations.findSpecifiedAnt(Constant.DATASTORE)
            findDataStoreAnt?.let {
                it.arguments.forEach { argument ->
                    when (argument.name?.asString()) {
                        DataStoreArgs.NAME -> name = argument.value.toString()
                        DataStoreArgs.ENABLED_LOG -> enabledLog = argument.value as Boolean
                        DataStoreArgs.AGGREGATING -> aggregating = argument.value as Boolean
                    }
                }
                generateData.data.add(dataStoreData)
            }
            classDeclaration.getAllProperties().forEach { it.accept(this@DataStoreVisitor, Unit) }
        }

        super.visitClassDeclaration(classDeclaration, data)
    }


    override fun visitPropertyDeclaration(property: KSPropertyDeclaration, data: Unit) {
        val argument = property.annotations.findSpecifiedAnt(Constant.DATA_WRITE)?.arguments?.first()
        argument?.let {
            generateData.data[index].apply {
                val storageValue = it.value.toString() //msg
                val storageKey = storageValue.reConstName() //MSG
                this.storageMap[storageKey] = storageValue
            }
        }
        val objectRelationArgument = property.annotations.findSpecifiedAnt(Constant.OBJECT_RELATION)
        val type = property.type.resolve()
        if (type.declaration is KSClassDeclaration && objectRelationArgument != null) (type.declaration as KSClassDeclaration).accept(this, data) //        recursiveSearch(property, data)
        super.visitPropertyDeclaration(property, data)
    }


    /**
     * 递归搜索DataWrite注解
     */
    private fun recursiveSearch(property: KSPropertyDeclaration, data: Unit) { // 检查属性类型
        val type = property.type.resolve() //如果属性是一个类，则继续递归访问该类的属性
        if (type.declaration is KSClassDeclaration) { // 检查是否为集合类型（如 List、Set 等）
            if (type.declaration.qualifiedName?.asString() in listOf("kotlin.collections.List", "kotlin.collections.Set", "kotlin.collections.Map")) { // 处理泛型参数
                type.arguments.forEach { typeArgument ->
                    val argumentType = typeArgument.type?.resolve()
                    argumentType?.let {
                        if (it.declaration is KSClassDeclaration) { // 如果泛型参数是一个类，则递归访问
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