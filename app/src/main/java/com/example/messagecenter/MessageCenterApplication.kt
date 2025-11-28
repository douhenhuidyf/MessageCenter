package com.example.messagecenter

import android.app.Application
import android.content.Context
import com.bytedance.rheatrace.RheaTrace3

import com.example.messagecenter.data.AppContainer
import com.example.messagecenter.data.AppDataContainer


class MessageCenterApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        RheaTrace3.init(base)
    }
}