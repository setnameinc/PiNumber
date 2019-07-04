package com.setnameinc.pinumber.presenter

import android.util.Log
import com.setnameinc.pinumber.common.BaseMainActivityPresenter
import com.setnameinc.pinumber.resolvers.PiResolver
import com.setnameinc.pinumber.ui.MainActivityView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class MainActivityPresenter @Inject constructor(private val piResolver: PiResolver) : BaseMainActivityPresenter<MainActivityView>(),
    MainActivityPresenterInterface {

    private val TAG = this::class.java.simpleName

    private val disposeBag = CompositeDisposable()

    private val amountOfNumbers = PublishSubject.create<Long>()

    init {

        piResolver.initResolver(this)

    }

    override fun updateAmountOfNumbers(long: Long) {

        amountOfNumbers.onNext(long)

    }

    override fun subscribeToAmountOfNumbers(): Disposable = amountOfNumbers
        .subscribeOn(Schedulers.io())
        .subscribe {

            piResolver.startResolve(it)

        }

    override fun onStart() {

        disposeBag.add(view.initEditTextListener())
        disposeBag.add(subscribeToAmountOfNumbers())

    }

    override fun onStop() {

        disposeBag.clear()

    }

    override fun showPiProgress() {
        Log.i(TAG, "is loading")
        view.showPiProgress()
    }

    override fun hidePiProgress() {
        Log.i(TAG, "isn't loading")
        view.hidePiProgress()
    }

    override fun saveResult(result: Double) {
        Log.i(TAG, "save result")
        view.saveResult(result)

    }
}

interface MainActivityPresenterInterface : MainActivityPresenterPiResolver {

    fun subscribeToAmountOfNumbers(): Disposable

    fun updateAmountOfNumbers(long: Long)

}

interface MainActivityPresenterPiResolver {

    fun showPiProgress()
    fun hidePiProgress()
    fun saveResult(result: Double)

}
