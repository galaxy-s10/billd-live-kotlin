package com.example.billd_live_kotlin

import android.app.Service
import android.content.Intent
import android.os.IBinder

class ScreenRecordingService : Service() {

    override fun onCreate() {
        println("----onCreate")
        super.onCreate()
    }

    override fun onBind(intent: Intent?): IBinder? {
        println("----onBind")
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        println("----onStartCommand")
        return super.onStartCommand(intent, flags, startId)
    }

}