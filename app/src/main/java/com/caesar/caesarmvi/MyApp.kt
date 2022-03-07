package com.caesar.caesarmvi

import android.app.Application
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch

/**
 * Created by Caesar
 * email : caesarshao@163.com
 */
class MyApp:Application() {
    override fun onCreate() {
        super.onCreate()
        GlobalScope.launch(Dispatchers.IO) {
            GlobalReg.userIntent.consumeAsFlow().collect {
                when (it) {
                    is GlobalIntent.touchLoad -> {
                        GlobalReg.state.value = GlobalState.loadingNews
                        delay(4000)
                        GlobalReg.state.value = GlobalState.successReturn("应用项目中返回的数据")
                        delay(100)
                        GlobalReg.state.value = GlobalState.loadingFinish
                    }
                }
            }
        }
    }
}