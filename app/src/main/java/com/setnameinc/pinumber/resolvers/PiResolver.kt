package com.setnameinc.pinumber.resolvers

import android.util.Log
import com.setnameinc.pinumber.presenter.MainActivityPresenterPiResolver
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.inject.Inject
import kotlin.math.pow

class PiResolver @Inject constructor() : PiResolverInterface {

    private val TAG = this::class.java.simpleName

    private lateinit var presenterInterface: MainActivityPresenterPiResolver

    private val disposableBag = CompositeDisposable()

    override fun initResolver(mainActivityPresenterInterface: MainActivityPresenterPiResolver) {

        Log.i(TAG, "init presenter")

        presenterInterface = mainActivityPresenterInterface

    }

    override fun startResolve(amount: Long) {

        disposableBag.add(calculateInBackground(amount))

    }

    private fun calculateInBackground(long: Long): Disposable =
        Observable.fromCallable { calculate(long) }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { presenterInterface.showPiProgress() }
            .doOnTerminate { presenterInterface.hidePiProgress() }
            .subscribe {

                presenterInterface.saveResult(it)

                Log.i(TAG, "resolved")

            }


    private fun calculate(long: Long): Double {

        var totalAmount = 0
        var countInRound = 0

        for (i in 0..long) {

            val randomX = Random().nextFloat()
            val randomY = Random().nextFloat()

            if (Math.sqrt(randomX.toDouble().pow(2.toDouble()) + randomY.toDouble().pow(2.toDouble())) <= 1) {
                countInRound++
            }

            totalAmount++

        }

        return 4.toDouble() * countInRound / totalAmount

    }

}

interface PiResolverInterface {

    fun initResolver(mainActivityPresenterInterface: MainActivityPresenterPiResolver)
    fun startResolve(amount: Long)

}
