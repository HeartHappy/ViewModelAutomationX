package com.hearthappy.processor.log

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSType
import com.hearthappy.processor.utils.DateUtil

const val TAG_VMA = "VMA"

fun KSPLogger.printGenerateTook(count: Int, measureTimeMillis: Long) {
    this.warn("$TAG_VMA: ===================> Generate file count:$count,took:${DateUtil.formatTime(measureTimeMillis)} <===================")
}

fun KSPLogger.printParsing(count: Int) {
    this.warn("$TAG_VMA: =====>The parsing of $count ViewModelAutomation files has been completed<=====")
}

fun KSPLogger.printVma(enableLog: Boolean = true, msg: String) {
    if (enableLog) {
        this.warn("$TAG_VMA: $msg ")
    }
}

fun KSPLogger.printStart() {
    this.warn("========================================start=================================================")
}

fun KSPLogger.printEnd() {
    this.warn("======================================== end =================================================\n")
}

fun KSPLogger.printGenerateStart(enableLog: Boolean = true, fileName: String) {
    if (enableLog) {
        this.warn("======================================$fileName file Generate start==============================================")
    }
}

fun KSPLogger.printGenerateEnd(enableLog: Boolean = true, fileName: String) {
    if (enableLog) {
        this.warn("======================================$fileName file Generate  end ==============================================\n")
    }
}


fun KSPLogger.outDeclaration(enableLog: Boolean = true, type: KSType, sign: Int) {
    printVma(enableLog, "----------------------->Declaration start<-----------------------")
    type.declaration.apply {
        printVma(enableLog, "$sign,simpleName:${simpleName.asString()}")
        printVma(enableLog, "$sign,qualifiedName:${qualifiedName?.asString()}")
        printVma(enableLog, "$sign,packageName:${packageName.asString()}")
    }
    printVma(enableLog, "----------------------->Declaration end<-----------------------\n")
}