package com.example.billd_live_kotlin

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.awaitResponse
import java.net.SocketTimeoutException


class MainActivity : ComponentActivity() {
    private val REQUEST_CODE = 100
    private var mProjectionManager: MediaProjectionManager? = null
    private val RECORD_REQUEST_CODE = 129
    var textview: TextView? = null
    var mediaProjectionManager: MediaProjectionManager? = null
    var mediaProjection: MediaProjection? = null
    var mediaRecorder: MediaRecorder? = null
    fun checkScreenRecordPermission() {
        println("checkScreenRecordPermission")
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                RECORD_REQUEST_CODE
            )
            println("没有有录音权限")

        } else {
            // 权限已授予，可以读取文件
            println("有录音权限")
        }
    }

    override fun onStart() {
        println("onStart---")
        super.onStart()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        println("onActivityResult---")
        val intent = Intent(this, ScreenRecordingService::class.java)
        intent.putExtra("resultData", data)
        intent.putExtra("resultCode", resultCode)
        println("resultCoderesultCode6666")
        println(data)
        println(resultCode)
        if (data != null) {
            startForegroundService(intent)
            println("用户点了确定666")

        } else {
            println("用户点了取消666")
        }
    }

    override fun onActivityReenter(resultCode: Int, data: Intent?) {
        super.onActivityReenter(resultCode, data)
        println("onActivityReenter---")
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        println("---onCreate")
        val linearLayout = LinearLayout(this)
        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        suspend fun getdata() {
            return coroutineScope {
                try {
//                    val call = BilldApi().live.getLiveList()
                    val gson = Gson()
                    val params = HashMap<String, Any>()
                    params["id"] = 101
                    params["password"] = "1234567"
                    val json = gson.toJson(params)
                    val requestBody =
                        RequestBody.create("application/json".toMediaTypeOrNull(), json)
                    val loginres = BilldApi().user.login(requestBody)
                    val response = loginres.awaitResponse()
                    if (response.isSuccessful) {
                        println(response.body())
                        println("isSuccessfulisSuccessful")
                        val loginres = BilldApi().user.login(requestBody)
                        val response = loginres.awaitResponse()
                    } else {
                        println("返回code不是200")
                        println(response.errorBody()?.string())
                    }
                } catch (e: SocketTimeoutException) {
                    println("错误")
                    println(e)
                }
            }
        }

        // 创建并添加按钮1
        val button1 = Button(this)
        button1.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        button1.text = "录屏"
        button1.setOnClickListener {
            // 处理按钮1的点击事件
            println("ddddddds===")
//            val call = BilldApi().live.getLiveList()
            GlobalScope.launch(Dispatchers.Main) {
//                val response = withContext(Dispatchers.IO) {
//                    val response = call.execute()
//                    println("---")
//                    println(response.body())
//                }
//                println("Response: $response")
                getdata()
            }

            println("9999")


//            MyWebrtc(applicationContext);
//            checkScreenRecordPermission()
//            val REQUEST_CODE_SCREEN_CAPTURE = 1
//            val screenCaptureIntent = mediaProjectionManager?.createScreenCaptureIntent()
//            startActivityForResult(screenCaptureIntent!!, REQUEST_CODE_SCREEN_CAPTURE)
        }
        linearLayout.addView(button1)

        // 创建并添加按钮2
        val button2 = Button(this)
        button2.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        button2.text = "播放"
        button2.setOnClickListener {
            println("333333")
        }
        linearLayout.addView(button2)

        mediaProjectionManager =
            getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        setContentView(linearLayout)
        //setContentView(R.layout.billd_layout_one)
    }
}
