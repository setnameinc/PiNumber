package com.setnameinc.pinumber.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.setnameinc.pinumber.App
import com.setnameinc.pinumber.R
import com.setnameinc.pinumber.common.BaseMainActivity
import com.setnameinc.pinumber.common.BasePresenter
import com.setnameinc.pinumber.common.BaseView
import com.setnameinc.pinumber.presenter.MainActivityPresenter
import com.setnameinc.pinumber.utils.rxutils.RxSearchObservable
import com.setnameinc.pinumber.viewmodels.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MainActivity : BaseMainActivity() {

    private val TAG = this::class.java.simpleName

    @Inject
    lateinit var presenter: MainActivityPresenter

    override fun inject() {
        App.appComponent.inject(this)
    }

    override fun getPresenter(): BasePresenter<*> = presenter

    private lateinit var viewModel: ViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.i(TAG, "onCreate")

        viewModel = ViewModelProviders.of(this)[ViewModel::class.java]

        initProgressBarListener()
        initSwitchListener()
        initResultListener()

    }

    override fun initProgressBarListener(){
        viewModel.progressIsVisible.observe(this, Observer {

            if (it){

                activity_main__progress_bar.setVisible()

            } else {

                activity_main__progress_bar.setInvisible()

            }

        })
    }

    override fun initSwitchListener() {

        fun setChecked() {

            activity_main__switch_tv.text = "so hot, but slow"
            viewModel.switchBeautyMod = true
            activity_main__pi_calc.visibility = View.VISIBLE

        }

        fun setNotChecked() {

            activity_main__switch_tv.text = "ugly, but so fast"
            viewModel.switchBeautyMod = false
            activity_main__pi_calc.visibility = View.INVISIBLE

        }

        if (viewModel.switchBeautyMod) {

            setChecked()

        } else {

            setNotChecked()

        }

        activity_main__switch.setOnCheckedChangeListener { _, isChecked ->

            if (isChecked) {

                setChecked()

            } else {

                setNotChecked()

            }

        }
    }

    override fun initResultListener() {
        viewModel.result.observe(this, Observer {

            activity_main__result_field.text = "$it"

        })
    }

    override fun initEditTextListener(): Disposable = RxSearchObservable.fromView(activity_main__til_field)
        .debounce(1500, TimeUnit.MILLISECONDS)
        .filter {
            it.isNotEmpty() && it.toLong() != viewModel.amount
        }
        .distinctUntilChanged()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe {

            Log.i(TAG, "EditorListener | listener invoked")

            presenter.updateAmountOfNumbers(it.toLong())

            viewModel.amount = it.toLong()

        }


    override fun saveResult(result: Double) {

        viewModel.result.value = result

        Log.i(TAG, "SaveResult | save result = $result")

    }

    override fun hideProgress() {

        Log.i(TAG, "ProgressBar | hide progress bar")

        if (viewModel.switchBeautyMod) {

            activity_main__pi_calc.stopDrawing()

        } else {

            viewModel.progressIsVisible.value = false

        }

        activity_main__switch.isClickable = true

    }

    override fun showProgress() {

        Log.i(TAG, "ProgressBar | show progress bar")

        if (viewModel.switchBeautyMod) {

            activity_main__pi_calc.drawPoints()

        } else {

            viewModel.progressIsVisible.value = true

        }

        activity_main__switch.isClickable = false

    }

    private fun View.setVisible(){

        this.visibility = View.VISIBLE

    }

    private fun View.setInvisible(){

        this.visibility = View.INVISIBLE

    }

}

interface MainActivityView : BaseView, ViewModelInteractions, MainActivityViewPiProgress {

    fun initEditTextListener(): Disposable
    fun initResultListener()
    fun initSwitchListener()
    fun initProgressBarListener()

}

interface MainActivityViewPiProgress {

    fun showProgress()
    fun hideProgress()

}

interface ViewModelInteractions {

    fun saveResult(result: Double)

}
