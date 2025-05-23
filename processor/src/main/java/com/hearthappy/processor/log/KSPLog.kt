package com.hearthappy.processor.log

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSType
import com.hearthappy.processor.utils.DateUtil

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
            logger.warn("$TAG_VMA: ===================> Generate ViewModel file count:$count,took:${DateUtil.formatTime(measureTimeMillis)} <===================")
        }

        fun printGenerateDataStoreTook(count: Int, measureTimeMillis: Long) {
            logger.warn("$TAG_DATASTORE:==============> Generate DataStore file count:$count,took:${DateUtil.formatTime(measureTimeMillis)} <===================")
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


        fun printStart() {
            logger.warn("========================================start=================================================")
        }

        fun printEnd() {
            logger.warn("======================================== end =================================================\n")
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