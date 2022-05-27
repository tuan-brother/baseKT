package com.hnltd.basekt.ui

import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.hnltd.basekt.data.Const

abstract class BaseActivity<ViewBinding : ViewDataBinding> : AppCompatActivity() {

    var mProgressDialog: ProgressDialog? = null
    var isNetworkState: Boolean = false
    lateinit var mBinding: ViewBinding

    //Listener lắng nghe sự thay đổi trạng thái connect internet
    private var onNetworkConnectedListener: OnNetworkConnectedListener? = null
    fun setOnNetworkConnectedListener(onNetworkConnectedListener: OnNetworkConnectedListener) {
        this.onNetworkConnectedListener = onNetworkConnectedListener
    }

    abstract fun getResource(): Int
    abstract fun onCreateActivity(savedInstanceState: Bundle?)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerBroadcastReciver()
        mBinding = DataBindingUtil.setContentView(this, getResource())
        mBinding.apply {
            mBinding.lifecycleOwner = this@BaseActivity
            root.isClickable = true
            executePendingBindings()
        }
        onCreateActivity(savedInstanceState)
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
    }

    fun showProgressLoading() {
        if (mProgressDialog != null && mProgressDialog!!.isShowing) {
            mProgressDialog!!.dismiss()
            mProgressDialog = null
        }
        mProgressDialog = ProgressDialog(this)
        mProgressDialog!!.setIndeterminate(true)
        mProgressDialog!!.setMessage("Loading")
        mProgressDialog!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        mProgressDialog!!.show()
    }

    fun updateMessageProgressDialog(message: String) {
        if (mProgressDialog != null && mProgressDialog!!.isShowing()) {
            mProgressDialog!!.setMessage(message)
        }
    }

    fun dissmisProgressLoading() {
        if (mProgressDialog != null && mProgressDialog!!.isShowing()) {
            mProgressDialog!!.dismiss()
        }
    }

    //Đăng ký broadcast lắng nghe sự kiện thay đổi network
    private fun registerBroadcastReciver() {
        val filter = IntentFilter()
        filter.addAction(Const.ACTION_NETWORK_CHANGE)
        registerReceiver(receiver, filter)
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action != null) {
                when (action) {
                    Const.ACTION_NETWORK_CHANGE -> {
                        isNetworkState = isNetworkConnected()
                        if (isNetworkState) {
                            onNetworkConnectedListener?.onNetworkConnected()
                        } else {
                            onNetworkConnectedListener?.onNetworkDisconnect()
                        }
                    }
                }
            }
        }
    }

    //Kiểm tra trạng thái internet
    fun isNetworkConnected(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetInfor = connectivityManager.activeNetworkInfo
        isNetworkState = activeNetInfor != null && activeNetInfor.isConnected
        return isNetworkState
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    interface OnNetworkConnectedListener {
        fun onNetworkConnected()
        fun onNetworkDisconnect()
    }
}