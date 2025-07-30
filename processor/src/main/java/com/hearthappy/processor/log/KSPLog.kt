package com.hearthappy.processor.log

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSType
import com.hearthappy.processor.utils.DateUtil

/**
 * @author ChenRui
 * ClassDescription： 日志输出格式化
 */
class KSPLog {
    companion object {
        private const val TAG_VMA = "VMA"
        private const val TAG_DATASTORE = "DataStore"
        private lateinit var logger: KSPLogger

        //ע: ARouter::Compiler >>> Start categories, group = web, path = /web/webPage <<<
        fun init(logger: KSPLogger) {
            Companion.logger = logger
        }

        fun printGenerateVMATook(count: Int, measureTimeMillis: Long) {
            isInit { logger.warn("$TAG_VMA: ===================> Generate ViewModel file count:$count,took:${DateUtil.formatTime(measureTimeMillis)} <===================\n") }
        }

        fun printGenerateDataStoreTook(count: Int, measureTimeMillis: Long) {
            isInit { logger.warn("$TAG_DATASTORE:==============> Generate DataStore file count:$count,took:${DateUtil.formatTime(measureTimeMillis)} <===================\n") }
        }

        fun printGenerateVMA(fileName: String, map: List<String>) {
            isInit {
                logger.warn("$TAG_VMA: =====> Generate ViewModel file >>> $fileName")
                logger.warn("$TAG_VMA: =====> methods >>> $map")
            }
        }

        fun printParsing(count: Int) {
            isInit { logger.warn("$TAG_VMA: =====> The parsing of $count ViewModelAutomation files has been completed<=====")  }
        }

        fun printVma(enableLog: Boolean = true, msg: String) {
            if (enableLog) {
                isInit { logger.warn("$TAG_VMA: $msg ") }
            }
        }

        fun printDataStore(enableLog: Boolean = true, msg: String) {
            if (enableLog) {
                isInit { logger.warn("$TAG_DATASTORE: $msg ") }
            }
        }

        fun printGenerateDataStore(properties: String, preferencesKeysName: String) {
            isInit { logger.warn("$TAG_DATASTORE:=====> Generate Properties: $properties >>> PreferencesKeys class: $preferencesKeysName") }
        }


        fun printStart(tag: String) {
            isInit { logger.warn("$tag:=====> start") }
        }

        fun printEnd(tag: String) {
            isInit { logger.warn("$tag:=====> end  \n") }
        }

        fun printGenerateStart(fileName: String) {
            isInit { logger.warn("======================================$fileName file Generate start==============================================") }
        }

        fun printGenerateEnd(fileName: String) {
            isInit { logger.warn("======================================$fileName file Generate  end ==============================================\n") }
        }


        fun outDeclaration(enableLog: Boolean = true, type: KSType, sign: Int) {
            isInit { printVma(enableLog, "----------------------->Declaration start<-----------------------")
                type.declaration.apply {
                    printVma(enableLog, "$sign,simpleName:${simpleName.asString()}")
                    printVma(enableLog, "$sign,qualifiedName:${qualifiedName?.asString()}")
                    printVma(enableLog, "$sign,packageName:${packageName.asString()}")
                }
                printVma(enableLog, "----------------------->Declaration end<-----------------------\n") }
        }

        private fun isInit(block: () -> Unit) {
            if (::logger.isInitialized) block()
        }
    }

}