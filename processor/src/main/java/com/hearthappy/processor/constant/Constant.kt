package com.hearthappy.processor.constant

object Constant {

    //注解
    const val VIEW_MODEL_AUTOMATION = "ViewModelAutomation"
    const val BIND_STATE_FLOW = "BindStateFlow"
    const val BIND_LIVE_DATA = "BindLiveData"
    const val DATA_WRITE = "DataWrite"
    const val DATASTORE = "DataStore"
    const val OBJECT_RELATION = "ObjectRelation"
    const val FILE_STORAGE = "FileStorage"

    //生成路径
    const val GENERATE_VIEWMODEL_PKG = "com.hearthappy.vma.generate.viewmodel"
    const val GENERATE_DATASTORE_PKG = "com.hearthappy.vma.generate.datastore"

    //应用包
    const val APPLICATION_PKG = "android.app"
    const val CONTEXT_PKG = "android.content"
    const val APPLICATION = "Application"
    const val CONTEXT = "Context"
    const val ANDROIDX_LIFECYCLE_PKG = "androidx.lifecycle"
    const val ANDROID_VIEW_MODEL = "AndroidViewModel"
    const val MUTABLE_LIVEDATA = "MutableLiveData"
    const val LIVEDATA = "LiveData"
    const val STATE_FLOW_PKG = "kotlinx.coroutines.flow"
    const val MUTABLE_STATE_FLOW = "MutableStateFlow"
    const val STATE_FLOW = "StateFlow"

    //网络包
    const val NETWORK_PKG = "com.hearthappy.vma_ktx.network"
    const val REQUEST_SCOPE = "requestScope"
    const val FLOW_RESULT = "FlowResult"
    const val LIVE_DATA_RESULT = "Result"

    //DataStore相关包
    const val DATASTORE_PKG = "androidx.datastore.core"
    const val DATASTORE_PREFERENCES_CORE_PKG = "androidx.datastore.preferences.core"
    const val DATASTORE_PREFERENCES_PKG = "androidx.datastore.preferences"


    //其他常用命名
    const val APP = "app"

    //转义符
    const val INDENTATION = "\t"

    //默认值:
    const val WRITE_DEFAULT = "value is empty"
    const val READ_DEFAULT = "The value of key was not found"


    //修改DataStore生成文件路径
    //将PreferencesKeys对象写入对应的Ext文件中

}
