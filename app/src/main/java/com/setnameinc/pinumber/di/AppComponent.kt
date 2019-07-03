package com.setnameinc.pinumber.di

import com.setnameinc.pinumber.ui.MainActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ViewModelModule::class])
interface AppComponent{

    fun inject(mainActivity: MainActivity)

}