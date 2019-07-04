package com.setnameinc.pinumber

import android.app.Application
import android.util.Log
import com.setnameinc.pinumber.di.AppComponent
import com.setnameinc.pinumber.di.DaggerAppComponent

class App : Application() {

    private val TAG = this::class.java.simpleName

    override fun onCreate() {
        super.onCreate()

        Log.i(TAG, "onCreate")

        appComponent = DaggerAppComponent
            .builder()
            .build()

    }

    companion object{

        lateinit var appComponent: AppComponent

    }

}