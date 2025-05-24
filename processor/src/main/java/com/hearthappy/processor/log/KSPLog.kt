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
        internal const val TAG_VMA = "VMA"
        internal const val TAG_DATASTORE = "DataStore"
        private lateinit var logger: KSPLogger

        //ע: ARouter::Compiler >>> Start categories, group = web, path = /web/webPage <<<
        fun init(logger: KSPLogger) {
            Companion.logger = logger
        }

        fun printGenerateVMATook(count: Int, measureTimeMillis: Long) {
            logger.warn("$TAG_VMA: ===================> Generate ViewModel file count:$count,took:${DateUtil.formatTime(measureTimeMillis)} <===================\n")
        }

        fun printGenerateDataStoreTook(count: Int, measureTimeMillis: Long) {
            logger.warn("$TAG_DATASTORE:==============> Generate DataStore file count:$count,took:${DateUtil.formatTime(measureTimeMillis)} <===================\n")
        }

        fun printGenerateVMA(fileName: String, map: List<String>) {
            logger.warn("$TAG_VMA: =====> Generate ViewModel file:$fileName,>>> methods:$map")
        }

        fun printParsing(count: Int) {
            logger.warn("$TAG_VMA: =====> The parsing of $count ViewModelAutomation files has been completed<=====")
        }

        fun printVma(enableLog: Boolean = true, msg: String) {
            if (enableLog) {
                logger.warn("$TAG_VMA: $msg ")
            }
        }

        fun printDataStore(enableLog: Boolean = true, msg: String) {
            if (enableLog) {
                logger.warn("$TAG_DATASTORE: $msg ")
            }
        }

        fun printGenerateDataStore(properties: String, preferencesKeysName: String) {
            logger.warn("$TAG_DATASTORE:=====> Generate Properties: $properties,>>> PreferencesKeys class:  $preferencesKeysName")
        }


        fun printStart(tag: String) {
            logger.warn("$tag:=====> start")
        }

        fun printEnd(tag: String) {
            logger.warn("$tag:=====> end  \n")
        }

        fun printGenerateStart(fileName: String) {
            logger.warn("======================================$fileName file Generate start==============================================")
        }

        fun printGenerateEnd(fileName: String) {
            logger.warn("======================================$fileName file Generate  end ==============================================\n")
        }


        fun outDeclaration(enableLog: Boolean = true, type: KSType, sign: Int) {
            printVma(enableLog, "----------------------->Declaration start<-----------------------")
            type.declaration.apply {
                printVma(enableLog, "$sign,simpleName:${simpleName.asString()}")
                printVma(enableLog, "$sign,qualifiedName:${qualifiedName?.asString()}")
                printVma(enableLog, "$sign,packageName:${packageName.asString()}")
            }
            printVma(enableLog, "----------------------->Declaration end<-----------------------\n")
        }
    }

}