package com.setnameinc.pinumber

import android.app.Application
import com.setnameinc.pinumber.di.AppComponent
import com.setnameinc.pinumber.di.DaggerAppComponent

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent
            .builder()
            .build()

    }

    companion object{

        lateinit var appComponent: AppComponent

    }

}