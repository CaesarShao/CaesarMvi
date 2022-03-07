package com.caesar.caesarmvi.global

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caesar.caesarmvi.GlobalIntent
import com.caesar.caesarmvi.GlobalReg
import com.caesar.caesarmvi.GlobalState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import kotlin.random.Random


/**
 * Created by Caesar
 * email : caesarshao@163.com
 */
class GlobalViewModel() : ViewModel() {
    init {
//        viewModelScope.launch {
//            GlobalReg.userIntent.consumeAsFlow().collect {
//                when (it) {
//                    is GlobalIntent.touchLoad -> {
//                        loadNet()
//                    }
//                }
//            }
//        }
    }
//    private fun loadNet() {
//        GlobalReg.state.value = GlobalState.loadingNews
//        viewModelScope.launch(Dispatchers.IO) {
//            delay(2000)
//            val data = Random.nextInt(4)
//            GlobalReg.state.value = if (data == 0) {
//                GlobalState.failReturn("GlobalViewModel网络请求失败,错误数据显示:"+data)
//            } else {
//                GlobalState.successReturn("GlobalViewModel网络请求成功,数据返回:"+data)
//            }
//            delay(100)//这边因为MutableStateFlow的特性加个延迟
//            GlobalReg.state.value = GlobalState.loadingFinish
//        }
//    }
}