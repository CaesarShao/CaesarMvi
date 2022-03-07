package com.caesar.caesarmvi

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Created by Caesar
 * email : caesarshao@163.com
 */
object GlobalReg {
    val userIntent = Channel<GlobalIntent>()
    val state = MutableStateFlow<GlobalState>(GlobalState.loadingFinish)
}