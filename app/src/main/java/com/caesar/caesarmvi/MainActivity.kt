package com.caesar.caesarmvi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.caesar.caesarmvi.global.GlobalActivity
import com.caesar.caesarmvi.net.NewActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.btn_net).setOnClickListener {
            startActivity(Intent(this, NewActivity::class.java))
        }
        findViewById<Button>(R.id.btn_global).setOnClickListener {
            startActivity(Intent(this, GlobalActivity::class.java))
        }
        GlobalScope.launch {
            GlobalReg.state.collect {
                when (it) {
                    is GlobalState.loadingNews -> {
                        Log.i("caesar","MainActivity中接收到了loadingNews")
                    }
                    is GlobalState.loadingFinish -> {
                        Log.i("caesar","MainActivity中接收到了loadingFinish")
                    }
                    is GlobalState.successReturn -> {
                        Log.i("caesar","MainActivity中接收到了successReturn:"+it.str)
                    }
                    is GlobalState.failReturn -> {
                        Log.i("caesar","MainActivity中接收到了failReturn")
                    }
                    else -> {

                    }
                }
            }
        }
    }
}