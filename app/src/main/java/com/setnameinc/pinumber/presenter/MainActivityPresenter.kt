package com.setnameinc.pinumber.presenter

import android.util.Log
import com.setnameinc.pinumber.common.BaseMainActivityPresenter
import com.setnameinc.pinumber.ui.MainActivityView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.lang.Math.sqrt
import java.util.*
import javax.inject.Inject
import kotlin.math.pow

class MainActivityPresenter @Inject constructor() : BaseMainActivityPresenter<MainActivityView>(),
    MainActivityPresenterInterface {

    private val TAG = this::class.java.simpleName

    private val disposeBag = CompositeDisposable()

    private val amountOfNumbers = PublishSubject.create<Long>()

    override fun updateAmountOfNumbers(long: Long) {

        amountOfNumbers.onNext(long)

    }

    fun test(){
        Log.i(TAG, "test")
    }

    override fun subscribeToAmountOfNumbers(): Disposable = amountOfNumbers
        .subscribeOn(Schedulers.io())
        .subscribe {

            disposeBag.add(calculateInBackground(it))

        }

    override fun calculateInBackground(long: Long): Disposable =
        Observable.just(long)
            .flatMap { Observable.fromCallable { calculate(it) } }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { view.showPiProgress() }
            .doOnTerminate { view.hidePiProgress() }
            .subscribe {

                view.saveResult(it)
                view.showResult(it)

            }

    override fun calculate(long: Long): Double {

        Log.i(TAG, "calculate, amount = $long")

        var totalAmount = view.getTotalAmount()
        var countInRound = view.getInRound()

        for (i in 0..long) {

            view.saveLeft(long-i)

            val randomX = Random().nextFloat()
            val randomY = Random().nextFloat()

            if (sqrt(randomX.toDouble().pow(2.toDouble()) + randomY.toDouble().pow(2.toDouble())) <= 1) {
                countInRound++
                view.saveInRound(countInRound)
            }

            totalAmount++
            view.saveTotalAmount(totalAmount)

        }

        return 4.toDouble() * view.getInRound() / view.getTotalAmount()

    }


    override fun onStart() {

        disposeBag.add(view.initEditTextListener())
        disposeBag.add(subscribeToAmountOfNumbers())

    }

    override fun onStop() {

        disposeBag.clear()

    }


}

interface MainActivityPresenterInterface : MainActivityPresenterCalculator {

    fun subscribeToAmountOfNumbers(): Disposable

    fun updateAmountOfNumbers(long: Long)

}

interface MainActivityPresenterCalculator {

    fun calculateInBackground(long: Long): Disposable

    fun calculate(long: Long): Double

}
