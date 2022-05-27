package com.hnltd.basekt.ui

import androidx.lifecycle.ViewModel
import com.hnltd.basekt.utils.SingleLiveData
import java.net.ConnectException
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.UnknownHostException

open class BaseViewModel : ViewModel() {
    // error message
    val errorMessage by lazy { SingleLiveData<String>() }

    // optional flags
    val noInternetConnectionEvent by lazy { SingleLiveData<Unit>() }
    val connectTimeoutEvent by lazy { SingleLiveData<Unit>() }
    val forceUpdateAppEvent by lazy { SingleLiveData<Unit>() }
    val serverMaintainEvent by lazy { SingleLiveData<Unit>() }
    val unknownErrorEvent by lazy { SingleLiveData<Unit>() }

    protected open fun onError(throwable: Throwable) {
        when (throwable) {
            // case no internet connection
            is UnknownHostException -> {
                noInternetConnectionEvent.call()
            }
            is ConnectException -> {
                noInternetConnectionEvent.call()
            }
            // case request time out
            is SocketTimeoutException -> {
                connectTimeoutEvent.call()
            }
            else -> {
                // convert throwable to base exception to get error information
//                val baseException = throwable.toBaseException()
//                when (baseException.httpCode) {
//                    HttpURLConnection.HTTP_UNAUTHORIZED -> {
//                        errorMessage.value = baseException.message
//                    }
//                    HttpURLConnection.HTTP_INTERNAL_ERROR -> {
//                        errorMessage.value = baseException.message
//                    }
//                    else -> {
//                        unknownErrorEvent.call()
//                    }
//                }
            }
        }
    }
}