package com.caesar.caesarmvi.net

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import kotlin.random.Random


/**
 * Created by Caesar
 * email : caesarshao@163.com
 */
class NewViewModel() : ViewModel() {
    val userIntent = Channel<NewIntent>()
    val state = MutableStateFlow<NewState>(NewState.loadingFinish)
    init {
        viewModelScope.launch {
            userIntent.consumeAsFlow().collect {
                when (it) {
                    is NewIntent.touchLoad -> {
                        loadNet()
                    }
                }
            }
        }
    }
    private fun loadNet() {
        state.value = NewState.loadingNews
        viewModelScope.launch(Dispatchers.IO) {
            delay(3000)
            val data = Random.nextInt(4)
            state.value = if (data == 0) {
                NewState.failReturn("网络请求失败,错误数据显示:"+data)
            } else {
                NewState.successReturn("网络请求成功,数据返回:"+data)
            }
            delay(100)//这边因为MutableStateFlow的特性加个延迟
            state.value = NewState.loadingFinish
        }
    }
}