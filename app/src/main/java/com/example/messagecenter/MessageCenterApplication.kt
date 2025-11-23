package com.example.messagecenter

import android.app.Application

import com.example.messagecenter.data.AppContainer
import com.example.messagecenter.data.AppDataContainer


class MessageCenterApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
        container.initializeSampleData()

    }
}