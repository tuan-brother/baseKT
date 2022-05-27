package com.hnltd.basekt

import android.app.AlertDialog
import android.app.WallpaperManager
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.hnltd.basekt.databinding.ActivityMainBinding
import com.hnltd.basekt.ui.BaseActivity
import java.io.IOException


class MainActivity : BaseActivity<ActivityMainBinding>() {

    val myWallpaperManager: WallpaperManager by lazy {
        WallpaperManager.getInstance(
            applicationContext
        )
    }
    var dialog: AlertDialog? = null
    private val CHOOSE_IMAGE = 22
    var options = arrayOf(
        "Home Screen",
        "Lock Screen",
        "Both"
    )

    override fun getResource(): Int = R.layout.activity_main

    override fun onCreateActivity(savedInstanceState: Bundle?) {
        setOnNetworkConnectedListener(object : BaseActivity.OnNetworkConnectedListener {
            override fun onNetworkConnected() {
                Toast.makeText(this@MainActivity, "Đã kết nối data", Toast.LENGTH_SHORT).show()
            }

            override fun onNetworkDisconnect() {
                Toast.makeText(this@MainActivity, "Đã mất mạng", Toast.LENGTH_SHORT).show()
            }

        })
        mBinding.apply {
            btnSetWallpaper.setOnClickListener {
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(Intent.createChooser(intent, "choose image"), CHOOSE_IMAGE)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CHOOSE_IMAGE) {
            val mCropImageUri = data?.data
            var bitmap: Bitmap? = null
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, mCropImageUri)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            val builder: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)
            builder.setTitle("Choose from below")
            val finalBitmap = bitmap
            builder.setItems(options) { dialogInterface, i ->
                val selectedItem: String = options[i]
                if (selectedItem == options[0]) {
                    try {
                        myWallpaperManager.setBitmap(
                            finalBitmap,
                            null,
                            false,
                            WallpaperManager.FLAG_SYSTEM
                        )
                        Toast.makeText(
                            this@MainActivity,
                            "Wallpaper set successfully!",
                            Toast.LENGTH_SHORT
                        ).show()
                        dialog?.dismiss()
                    } catch (e: Exception) {
                    }
                } else if (selectedItem == options[1]) {
                    try {
                        myWallpaperManager.setBitmap(
                            finalBitmap,
                            null,
                            false,
                            WallpaperManager.FLAG_LOCK
                        )
                        Toast.makeText(
                            this@MainActivity,
                            "Wallpaper set successfully!",
                            Toast.LENGTH_SHORT
                        ).show()
                        dialog?.dismiss()
                    } catch (e: Exception) {
                    }
                } else if (selectedItem == options[2]) {
                    try {
                        myWallpaperManager.setBitmap(
                            finalBitmap,
                            null,
                            false,
                            WallpaperManager.FLAG_LOCK or WallpaperManager.FLAG_SYSTEM
                        )
                        Toast.makeText(
                            this@MainActivity,
                            "Wallpaper set successfully!",
                            Toast.LENGTH_SHORT
                        ).show()
                        dialog?.dismiss()
                    } catch (e: Exception) {
                    }
                }
            }
            dialog = builder.create()
            dialog?.show()
        }
    }
}