package com.hearthappy.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.validate
import com.hearthappy.annotations.storage.DataStore
import com.hearthappy.processor.constant.Constant
import com.hearthappy.processor.datahandler.DataCheck.isEmpty
import com.hearthappy.processor.datahandler.reFileName
import com.hearthappy.processor.datahandler.rePreferencesKeysName
import com.hearthappy.processor.datahandler.rename
import com.hearthappy.processor.exceptions.GenerateException
import com.hearthappy.processor.ext.AndroidTypeNames
import com.hearthappy.processor.ext.DataStoreTypeNames
import com.hearthappy.processor.ext.KotlinTypeNames
import com.hearthappy.processor.generate.IPoetFactory
import com.hearthappy.processor.generate.impl.PoetFactory
import com.hearthappy.processor.log.printDataStore
import com.hearthappy.processor.model.GenerateDataStoreData
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.KModifier
/**
 * @author ChenRui
 * ClassDescription： DataStore Symbol Processing
 */
class DataStoreSymbolProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return DataStoreSymbolProcessor(environment.logger, environment.codeGenerator, PoetFactory())
    }

    inner class DataStoreSymbolProcessor(val logger: KSPLogger, val codeGenerator: CodeGenerator, val dataStoreFactory: IPoetFactory) : SymbolProcessor {
        private val generateDataStoreData = GenerateDataStoreData()

        override fun process(resolver: Resolver): List<KSAnnotated> {
            val dsSymbols = resolver.getSymbolsWithAnnotation(DataStore::class.qualifiedName!!).filter { it.validate() }
            if (dsSymbols.isEmpty()) return emptyList()
            parsingDSProcess(dsSymbols, resolver)
            generateDSProcess()
            return emptyList()
        }

        private fun generateDSProcess() {
            generateDataStoreData.data.forEach {
                logger.printDataStore(it.enabledLog, "$it")
                dataStoreFactory.apply {
                    createFileSpec(it.name.reFileName(), Constant.GENERATE_DATASTORE_PKG).apply {
                        addSpecProperty(it.name.rename(), it.propertyType, AndroidTypeNames.Context, true, CodeBlock.of("%T(name = %S)", DataStoreTypeNames.DataStorePreferences, it.name), KModifier.PUBLIC)
                        val classSpec = createObjectSpec(it.name.rePreferencesKeysName())
                        it.storageMap.forEach { map ->
                            classSpec.addSpecProperty(map.key, KotlinTypeNames.String, null, false, CodeBlock.of("%S", map.value))
                        }
                        it.containingFile?.let { cf ->
                            buildAndWrite(classSpec.build(), cf, codeGenerator)
                        } ?: throw GenerateException("Source file not found")
                    }
                }
            }
        }

        private fun parsingDSProcess(dsSymbols: Sequence<KSAnnotated>, resolver: Resolver) {
            dsSymbols.forEachIndexed { index, ksAnnotated -> ksAnnotated.accept(DataStoreVisitor(resolver, logger, generateDataStoreData, index), Unit) }
        }
    }
}