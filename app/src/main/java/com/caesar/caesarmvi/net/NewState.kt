package com.caesar.caesarmvi.net

/**
 * Created by Caesar
 * email : caesarshao@163.com
 */
sealed class NewState {
    object loadingNews : NewState()
    object loadingFinish : NewState()
    data class successReturn(val str: String) : NewState()
    data class failReturn(val str: String) : NewState()
}