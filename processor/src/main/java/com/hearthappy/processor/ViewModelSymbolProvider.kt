package com.hearthappy.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
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
import com.hearthappy.processor.log.printEnd
import com.hearthappy.processor.log.printGenerateEnd
import com.hearthappy.processor.log.printGenerateStart
import com.hearthappy.processor.log.printGenerateVMATook
import com.hearthappy.processor.log.printParsing
import com.hearthappy.processor.log.printStart
import com.hearthappy.processor.model.GenerateViewModelData
import kotlin.system.measureTimeMillis

class ViewModelSymbolProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return ViewModelSymbolProcessor(
            environment.logger,
            environment.codeGenerator,
            GenerateVMAImpl(environment.logger)
        )
    }

    inner class ViewModelSymbolProcessor(
        private val logger: KSPLogger,
        private val codeGenerator: CodeGenerator,
        private val viewModelFactory: IVMAFactory
    ) :
        SymbolProcessor {
        private val viewModelData by lazy { GenerateViewModelData() }


        override fun process(resolver: Resolver): List<KSAnnotated> {

            val measureTimeMillis = measureTimeMillis {
                val vmaSymbols = resolver.getSymbolsWithAnnotation(ViewModelAutomation::class.qualifiedName!!)
                    .filter { it.validate() }
                if (vmaSymbols.isEmpty()) return emptyList()
                parsingVMAProcess(resolver, vmaSymbols, viewModelData)
                generateVMAProcess()
            }
            logger.printGenerateVMATook(viewModelData.data.size, measureTimeMillis)

            return emptyList()
        }


        private fun generateVMAProcess() {
            viewModelData.data.forEach {
                viewModelFactory.apply {
                    logger.printGenerateStart(it.enabledLog, it.className)
                    generateViewModel(it).apply {
                        generateProperty(it)
                        generateMethod(it)
                        generateAndWriteFile(it, codeGenerator)
                    }
                    logger.printGenerateEnd(it.enabledLog, it.className)
                }
            }
        }

        private fun parsingVMAProcess(
            resolver: Resolver,
            vmaSymbols: Sequence<KSAnnotated>,
            generateData: GenerateViewModelData,
        ) {
            logger.printStart()
            vmaSymbols.forEachIndexed { index, it -> it.accept(ViewModelVisitor(resolver, logger, generateData, index), Unit) }
            logger.printParsing(vmaSymbols.count())
            logger.printEnd()
        }

    }
}

