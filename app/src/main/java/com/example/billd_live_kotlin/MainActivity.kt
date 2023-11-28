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
import android.view.Menu
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController

import com.google.android.material.bottomnavigation.BottomNavigationView
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
import androidx.navigation.ui.AppBarConfiguration

import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.billd_live_kotlin.databinding.BilldLayoutOneBinding
import com.google.android.material.bottomnavigation.BottomNavigationMenuView

class MainActivity : AppCompatActivity() {
    private val REQUEST_CODE = 100
    private var mProjectionManager: MediaProjectionManager? = null
    private val RECORD_REQUEST_CODE = 129
    var textview: TextView? = null
    var mediaProjectionManager: MediaProjectionManager? = null
    var mediaProjection: MediaProjection? = null
    var mediaRecorder: MediaRecorder? = null

    private lateinit var binding: BilldLayoutOneBinding

    fun checkScreenRecordPermission() {
        println("checkScreenRecordPermission")
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.RECORD_AUDIO), RECORD_REQUEST_CODE
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

//    @RequiresApi(Build.VERSION_CODES.Q)
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        println("onActivityResult---")
//        val intent = Intent(this, ScreenRecordingService::class.java)
//        intent.putExtra("resultData", data)
//        intent.putExtra("resultCode", resultCode)
//        println("resultCoderesultCode6666")
//        println(data)
//        println(resultCode)
//        if (data != null) {
//            startForegroundService(intent)
//            println("用户点了确定666")
//
//        } else {
//            println("用户点了取消666")
//        }
//    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun onActivityResult2(requestCode: Int, resultCode: Int, data: Intent?) {
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = BilldLayoutOneBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        navView.itemIconTintList = null //切换不同图片时需要设置itemIconTintList为null

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val menu = navView.menu

//        val destinationChangedListener =
//            NavController.OnDestinationChangedListener { controller, destination, arg ->
//                // 处理目标改变事件
//                when (destination.id) {
//                    R.id.navigation_home -> {
//                        println("点了home")
//                        for (i in 0 until menu.size()) {
//                            val menuItem = menu.getItem(i)
//                            if (menuItem.itemId == destination.id) {
//                                // 找到了当前目标项
////                                val currentIndex = i
//                                // 在这里进行相应的操作，例如修改图标
//                                if(menuItem.isChecked){
//                                    menuItem.setIcon(R.drawable.area_active)
//
//                                }else{
//                                    menuItem.setIcon(R.drawable.home)
//
//                                }
//                                break
//                            }else{
//                                menuItem.setIcon(R.drawable.home)
//                            }
//                        }
////                        val home = findViewById<BottomNavigationMenuView>(R.id.navigation_home)
////                        val item = navView.menu.getItem(destination.id)
////                        item.setIcon(R.drawable.area_active)
//                    }
//
//                    R.id.navigation_dashboard -> {
//                        println("点了dashboadd")
//                    }
//                    // 添加其他目标的处理逻辑
//                    else -> {
//                        // 默认处理逻辑
//                        println("点了其他")
//
//                    }
//                }
//            }
//
//        navController.addOnDestinationChangedListener(destinationChangedListener)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_user, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("WrongConstant")
    fun onCreate2(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        println("---onCreate")
        val linearLayout = LinearLayout(this)
        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
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
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
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
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        button2.text = "播放"
        button2.setOnClickListener {
            println("333333")
        }
        linearLayout.addView(button2)

        mediaProjectionManager =
            getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
//        setContentView(linearLayout)
        setContentView(R.layout.billd_layout_one)
    }
}
