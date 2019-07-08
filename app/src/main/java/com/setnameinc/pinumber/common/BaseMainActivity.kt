package com.setnameinc.pinumber.common

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.setnameinc.pinumber.ui.MainActivityView

abstract class BaseMainActivity : AppCompatActivity(), MainActivityView {

    protected abstract fun getPresenter(): BasePresenter<*>
    protected abstract fun inject()

    override fun onCreate(savedInstanceState: Bundle?) {

        inject()
        getPresenter().view = this

        super.onCreate(savedInstanceState)


    }

    override fun onStart() {
        super.onStart()
        getPresenter().onStart()
    }

    override fun onStop() {
        super.onStop()
        getPresenter().onStop()
    }

}