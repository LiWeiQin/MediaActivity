package com.zkxt.mediausing

import androidx.multidex.MultiDexApplication
import com.arcns.core.APP

class ZkxtApplication : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        APP.INSTANCE = this
    }

}