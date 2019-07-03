package com.setnameinc.pinumber.ui

import android.os.Bundle
import android.util.Log
import android.view.View
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
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MainActivity : BaseMainActivity() {

    private val TAG = this::class.java.simpleName

    @Inject
    lateinit var mainActivityPresenter: MainActivityPresenter

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun inject() {
        App.appComponent.inject(this)
    }

    override fun getPresenter(): BasePresenter<*> = mainActivityPresenter

    private lateinit var viewModel: ViewModel

    private var isRestored = false

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProviders.of(this, viewModelFactory)[ViewModel::class.java]

        restoreViewModel()
        initResultListener()

    }

    private fun restoreViewModel() {

        isRestored = if (viewModel.result != RESULT_DEFAULT_VALUE) {

            Log.i(TAG, "Restore | restored")

            showResult(viewModel.result)

            true

        } else {

            false

        }

    }

    override fun initResultListener() {

        viewModel.amount.observe(this, Observer {

            Log.i(TAG, "ResultListener | observer invoked")

            //dirty
            if (!isRestored) {

                mainActivityPresenter.updateAmountOfNumbers(it)


            }

        })

    }

    override fun initEditTextListener(): Disposable = RxSearchObservable.fromView(activity_main__til_field)
        .debounce(1500, TimeUnit.MILLISECONDS)
        .distinctUntilChanged()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe {

            if (it.isNotEmpty()) {

                if (viewModel.amount.value != it.toLong()) {

                    Log.i(TAG, "EditorListener | non equals")

                    isRestored = false

                    viewModel.amount.value = it.toLong()

                }

            }

        }

    override fun showResult(result: Double) {

        activity_main__result_field.text = "$result"

    }

    override fun setResult(result: Double) {

        viewModel.result = result

    }

    override fun hideProgressBar() {

        Log.i(TAG, "ProgressBar | hide progress bar")

        activity_main__pi_calc.stopDrawing()

        /*activity_main__pb.visibility = View.INVISIBLE*/

    }

    override fun showProgressBar() {

        Log.i(TAG, "ProgressBar | show progress bar")

        activity_main__pi_calc.drawPoints()

        /*activity_main__pb.visibility = View.VISIBLE*/

    }

}

interface MainActivityView : BaseView {

    fun initEditTextListener(): Disposable
    fun initResultListener()

    fun showResult(result: Double)

    fun showProgressBar()
    fun hideProgressBar()

    fun setResult(result: Double)

}
