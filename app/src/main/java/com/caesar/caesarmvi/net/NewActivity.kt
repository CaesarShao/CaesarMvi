package com.caesar.caesarmvi.net

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.caesar.caesarmvi.R
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class NewActivity : AppCompatActivity() {
    var viewModel: NewViewModel? = null
    var srlShow: SwipeRefreshLayout? = null
    var tvShow: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new)
        viewModel = ViewModelProvider(this).get(NewViewModel::class.java)
        tvShow = findViewById(R.id.tv_show)
        srlShow = findViewById(R.id.srl_show)
        srlShow?.setOnRefreshListener {
            touchLoad()
        }
        viewModel?.viewModelScope?.launch {
            viewModel?.state?.collect {
                when (it) {
                    is NewState.loadingNews -> {
                        srlShow?.isRefreshing = true
                        tvShow?.text = "网络请求中.."
                    }
                    is NewState.loadingFinish -> {
                        srlShow?.isRefreshing = false
                    }
                    is NewState.successReturn -> {
                        tvShow?.text = it.str
                    }
                    is NewState.failReturn -> {
                        tvShow?.text = it.str
                    }
                    else -> {

                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        touchLoad()
    }

    private fun touchLoad() {
        viewModel?.viewModelScope?.launch {
            viewModel?.userIntent?.send(NewIntent.touchLoad)
        }
    }

}