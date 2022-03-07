package com.caesar.caesarmvi

/**
 * Created by Caesar
 * email : caesarshao@163.com
 */
sealed class GlobalState {
    object loadingNews : GlobalState()
    object loadingFinish : GlobalState()
    data class successReturn(val str: String) : GlobalState()
    data class failReturn(val str: String) : GlobalState()
}