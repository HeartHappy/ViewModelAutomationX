package com.hearthappy.processor.constant

import com.hearthappy.processor.constant.Constant.ANDROIDX_LIFECYCLE_PKG
import com.hearthappy.processor.constant.Constant.ANDROID_VIEW_MODEL
import com.hearthappy.processor.constant.Constant.APPLICATION
import com.hearthappy.processor.constant.Constant.APPLICATION_PKG
import com.hearthappy.processor.constant.Constant.FLOW_RESULT
import com.hearthappy.processor.constant.Constant.LIVEDATA
import com.hearthappy.processor.constant.Constant.LIVE_DATA_RESULT
import com.hearthappy.processor.constant.Constant.MUTABLE_LIVEDATA
import com.hearthappy.processor.constant.Constant.MUTABLE_STATE_FLOW
import com.hearthappy.processor.constant.Constant.NETWORK_PKG
import com.hearthappy.processor.constant.Constant.REQUEST_SCOPE
import com.hearthappy.processor.constant.Constant.STATE_FLOW
import com.hearthappy.processor.constant.Constant.STATE_FLOW_PKG
import com.squareup.kotlinpoet.ClassName

/**
 * 直接创建Classname
 */
object AndroidTypeNames {
    internal val Application = ClassName(APPLICATION_PKG, APPLICATION)
}


object LifecyclesTypeNames {
    internal val AndroidViewModel = ClassName(ANDROIDX_LIFECYCLE_PKG, ANDROID_VIEW_MODEL)
    internal val MutableStateFlow = ClassName(STATE_FLOW_PKG, MUTABLE_STATE_FLOW)
    internal val StateFlow = ClassName(STATE_FLOW_PKG, STATE_FLOW)
    internal val MutableLiveData = ClassName(ANDROIDX_LIFECYCLE_PKG, MUTABLE_LIVEDATA)
    internal val LiveData = ClassName(ANDROIDX_LIFECYCLE_PKG, LIVEDATA)
}

object VMANetworkTypeNames {
    internal val FlowResult = ClassName(NETWORK_PKG, FLOW_RESULT)
    internal val LiveDataResult = ClassName(NETWORK_PKG, LIVE_DATA_RESULT)
    internal val RequestScope = ClassName(NETWORK_PKG, REQUEST_SCOPE)
}


object KotlinTypeNames {
    val UNIT = ClassName("kotlin", "Unit")
    val CONTINUATION = ClassName("kotlin.coroutines", "Continuation")
    val COROUTINE_SCOPE = ClassName("kotlinx.coroutines", "CoroutineScope")
    val CHANNEL = ClassName("kotlinx.coroutines.channels", "Channel")
    val RECEIVE_CHANNEL = ClassName("kotlinx.coroutines.channels", "ReceiveChannel")
    val SEND_CHANNEL = ClassName("kotlinx.coroutines.channels", "SendChannel")
    val FLOW = ClassName("kotlinx.coroutines.flow", "Flow")
}