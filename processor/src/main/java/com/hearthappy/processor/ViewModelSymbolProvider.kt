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
import com.hearthappy.processor.generate.IGenerateFactory
import com.hearthappy.processor.generate.impl.GenerateFactory
import com.hearthappy.processor.log.printEnd
import com.hearthappy.processor.log.printGenerateEnd
import com.hearthappy.processor.log.printGenerateStart
import com.hearthappy.processor.log.printGenerateTook
import com.hearthappy.processor.log.printParsing
import com.hearthappy.processor.log.printStart
import com.hearthappy.processor.model.GenerateViewModelData
import kotlin.system.measureTimeMillis

class ViewModelSymbolProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return ViewModelSymbolProcessor(
            environment.logger,
            environment.codeGenerator
        )
    }
}

class ViewModelSymbolProcessor(
    private val logger: KSPLogger,
    private val codeGenerator: CodeGenerator
) :
    SymbolProcessor {


    override fun process(resolver: Resolver): List<KSAnnotated> {
        val generateData = GenerateViewModelData()
        val generateFactory: IGenerateFactory by lazy { GenerateFactory(logger) }
        val measureTimeMillis = measureTimeMillis {

            val vmaSymbols = resolver.getSymbolsWithAnnotation(ViewModelAutomation::class.qualifiedName!!).filter { it.validate() }
            if (vmaSymbols.count() == 0) return emptyList()
            parsingProcess(vmaSymbols, generateData)
            generateProcess(generateData, generateFactory)
        }
        logger.printGenerateTook(generateData.viewModelData.size, measureTimeMillis)
        return emptyList()
    }

    private fun generateProcess(generateData: GenerateViewModelData, generateFactory: IGenerateFactory) {
        generateData.viewModelData.forEach {
            generateFactory.apply {
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

    private fun parsingProcess(vmaSymbols: Sequence<KSAnnotated>, generateData: GenerateViewModelData) {
        logger.printStart()
        vmaSymbols.forEachIndexed { index, it -> it.accept(ViewModelVisitor(logger, generateData, index), Unit) }
        logger.printParsing(vmaSymbols.count())
        logger.printEnd()
    }

}