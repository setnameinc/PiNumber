package com.setnameinc.pinumber.ui

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.setnameinc.pinumber.App
import com.setnameinc.pinumber.R
import com.setnameinc.pinumber.common.BaseMainActivity
import com.setnameinc.pinumber.common.BasePresenter
import com.setnameinc.pinumber.common.BaseView
import com.setnameinc.pinumber.presenter.MainActivityPresenter
import com.setnameinc.pinumber.utils.constants.ViewModel.RESULT_DEFAULT_VALUE
import com.setnameinc.pinumber.utils.rxutils.RxSearchObservable
import com.setnameinc.pinumber.viewmodels.ViewModel
import io.reactivex.Observable
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

        initResultListener()

    }

    override fun initResultListener(){
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

            presenter.updateAmountOfNumbers(it.toInt())

            viewModel.amount = it.toLong()

        }


    override fun saveResult(result: Double) {

        viewModel.result.value = result

        Log.i(TAG, "SaveResult | save result = $result")

    }

    override fun hidePiProgress() {

        Log.i(TAG, "ProgressBar | hide progress bar")

        activity_main__pi_calc.stopDrawing()

    }

    override fun showPiProgress() {

        Log.i(TAG, "ProgressBar | show progress bar")

        activity_main__pi_calc.drawPoints()

    }

}

interface MainActivityView : BaseView, ViewModelInteractions, MainActivityViewPiProgress {

    fun initEditTextListener(): Disposable
    fun initResultListener()

}

interface MainActivityViewPiProgress {

    fun showPiProgress()
    fun hidePiProgress()

}

interface ViewModelInteractions {

    fun saveResult(result: Double)

}
