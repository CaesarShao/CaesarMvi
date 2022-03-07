package com.caesar.caesarmvi.global

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.caesar.caesarmvi.GlobalIntent
import com.caesar.caesarmvi.GlobalReg
import com.caesar.caesarmvi.GlobalState
import com.caesar.caesarmvi.R
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class GlobalActivity : AppCompatActivity() {
    var viewModel: GlobalViewModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_global)
        viewModel = ViewModelProvider(this).get(GlobalViewModel::class.java)
        findViewById<Button>(R.id.btn_show).setOnClickListener {
            touchLoad()
        }
        viewModel?.viewModelScope?.launch {
            GlobalReg.state.collect {
                when (it) {
                    is GlobalState.loadingNews -> {
                        Log.i("caesar","GlobalActivity中接收到了loadingNews")
                    }
                    is GlobalState.loadingFinish -> {
                        Log.i("caesar","GlobalActivity中接收到了loadingFinish")
                    }
                    is GlobalState.successReturn -> {
                        Log.i("caesar","GlobalActivity中接收到了successReturn:"+it.str)
                    }
                    is GlobalState.failReturn -> {
                        Log.i("caesar","GlobalActivity中接收到了failReturn")
                    }
                    else -> {

                    }
                }
            }
        }
    }

    private fun touchLoad() {
        viewModel?.viewModelScope?.launch {
            GlobalReg.userIntent.send(GlobalIntent.touchLoad)
        }
    }
}