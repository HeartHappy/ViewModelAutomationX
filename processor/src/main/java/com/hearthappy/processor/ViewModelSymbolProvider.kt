package com.hearthappy.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate
import com.hearthappy.annotations.ViewModelAutomation
import com.hearthappy.processor.datahandler.DataCheck.isEmpty
import com.hearthappy.processor.generate.IVMAFactory
import com.hearthappy.processor.generate.impl.GenerateVMAImpl
import com.hearthappy.processor.log.KSPLog
import com.hearthappy.processor.model.ViewModelData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis

/**
 * @author ChenRui
 * ClassDescription： ViewModel Symbol Processing
 */
class ViewModelSymbolProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        val enableLog = environment.options["enableVMALog"]?.toBoolean() ?: false
        if (enableLog) KSPLog.init(environment.logger)
        return ViewModelSymbolProcessor(environment.codeGenerator, GenerateVMAImpl())
    }

    inner class ViewModelSymbolProcessor(private val codeGenerator: CodeGenerator, private val viewModelFactory: IVMAFactory) : SymbolProcessor {


        override fun process(resolver: Resolver): List<KSAnnotated> {
            val vmaSymbols = resolver.getSymbolsWithAnnotation(ViewModelAutomation::class.qualifiedName!!).filter { it.validate() }
            if (vmaSymbols.isEmpty()) return emptyList()

            val declarationSequence = vmaSymbols.filterIsInstance<KSClassDeclaration>()

            val took = measureTimeMillis {
                runBlocking {
                    val channel = Channel<ViewModelData>(Channel.UNLIMITED)

                    val producer = producerJob(channel, declarationSequence, resolver)

                    val consumer = consumerJob(channel, declarationSequence.count())

                    joinAll(producer, consumer)
                }
            }
            KSPLog.printGenerateVMATook(declarationSequence.count(), took)
            return emptyList()
        }

        /**
         * 消费者协程：文件的生成
         * @receiver CoroutineScope
         * @param channel Channel<RouterInfo>
         * @param totalCount Int
         * @return Job
         */
        private fun CoroutineScope.consumerJob(channel: Channel<ViewModelData>, totalCount: Int): Job {
            return launch(Dispatchers.IO) {
                var count = 0
                channel.consumeEach {
                    generateVMAProcess(it)
                    if (++count == totalCount) channel.close()
                }
            }
        }

        /**
         * 生产者协程：符号解析
         * @receiver CoroutineScope
         * @param channel Channel<RouterInfo>
         * @param declarationSequence Sequence<KSClassDeclaration>
         * @return Job
         */
        private fun CoroutineScope.producerJob(channel: Channel<ViewModelData>, declarationSequence: Sequence<KSClassDeclaration>, resolver: Resolver): Job {
            return launch(Dispatchers.Default) {
                declarationSequence.forEach { it.accept(ViewModelVisitor(channel, this, resolver), Unit) }
            }
        }

        private fun generateVMAProcess(viewModelData: ViewModelData) {
            viewModelFactory.apply {
                generateViewModel(viewModelData).apply {
                    generateProperty(viewModelData)
                    generateMethod(viewModelData)
                    generateAndWriteFile(viewModelData, codeGenerator)
                    KSPLog.printGenerateVMA(viewModelData.className, viewModelData.functionList.map { it.methodName })
                }
            }
        }
    }
}

