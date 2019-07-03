package com.setnameinc.pinumber.presenter

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

    private val disposeBag = CompositeDisposable()

    private val amountOfNumbers = PublishSubject.create<Long>()

    override fun updateAmountOfNumbers(long: Long) {

        amountOfNumbers.onNext(long)

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
            .doOnSubscribe{view.showProgressBar()}
            .doOnTerminate{view.hideProgressBar()}
            .subscribe {

                view.setResult(it)
                view.showResult(it)

            }

    override fun calculate(long: Long): Double {

        var countOfAll = 0
        var countOfInRound = 0

        for (i in 0..long) {

            val randomX = Random().nextFloat()
            val randomY = Random().nextFloat()

            if (sqrt(randomX.toDouble().pow(2.toDouble()) + randomY.toDouble().pow(2.toDouble())) <= 1) {
                countOfInRound++
            }

            countOfAll++

        }

        return 4.toDouble() * countOfInRound / countOfAll

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

    fun calculateInBackground(long: Long) : Disposable

    fun calculate(long: Long): Double

}
