package com.hearthappy.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.validate
import com.hearthappy.annotations.ViewModelAutomation
import com.hearthappy.processor.datahandler.DataCheck.isEmpty
import com.hearthappy.processor.generate.IVMAFactory
import com.hearthappy.processor.generate.impl.GenerateVMAImpl
import com.hearthappy.processor.model.GenerateViewModelData
import com.hearthappy.processor.log.KSPLog
import kotlin.system.measureTimeMillis

/**
 * @author ChenRui
 * ClassDescription： ViewModel Symbol Processing
 */
class ViewModelSymbolProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        KSPLog.init(environment.logger)
        return ViewModelSymbolProcessor( environment.codeGenerator, GenerateVMAImpl())
    }

    inner class ViewModelSymbolProcessor( private val codeGenerator: CodeGenerator, private val viewModelFactory: IVMAFactory) : SymbolProcessor {
        private val viewModelData by lazy { GenerateViewModelData() }


        override fun process(resolver: Resolver): List<KSAnnotated> {
            val measureTimeMillis = measureTimeMillis {
                val vmaSymbols = resolver.getSymbolsWithAnnotation(ViewModelAutomation::class.qualifiedName!!).filter { it.validate() }
                if (vmaSymbols.isEmpty()) return emptyList()
                parsingVMAProcess(resolver, vmaSymbols, viewModelData)
                generateVMAProcess()
            }
            KSPLog.printGenerateVMATook(viewModelData.data.size, measureTimeMillis)
            return emptyList()
        }


        private fun generateVMAProcess() {
            viewModelData.data.forEach {
                viewModelFactory.apply {
                    generateViewModel(it).apply {
                        generateProperty(it)
                        generateMethod(it)
                        generateAndWriteFile(it, codeGenerator)
                        KSPLog.printGenerateVMA(it.className, it.functionList.map { it.methodName })
                    }
                }
            }
        }

        private fun parsingVMAProcess(
            resolver: Resolver,
            vmaSymbols: Sequence<KSAnnotated>,
            generateData: GenerateViewModelData,
        ) { //            logger.printStart()
            vmaSymbols.forEachIndexed { index, it -> it.accept(ViewModelVisitor(resolver, generateData, index), Unit) } //            logger.printParsing(vmaSymbols.count())
            //            logger.printEnd()
        }

    }
}

