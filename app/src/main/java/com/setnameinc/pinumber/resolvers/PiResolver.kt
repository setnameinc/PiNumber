package com.setnameinc.pinumber.resolvers

import android.util.Log
import com.setnameinc.pinumber.presenter.MainActivityPresenterPiResolver
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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

    override fun startResolve(amount: Int) {

        disposableBag.clear()
        disposableBag.add(calculateInBackground(amount))

    }

    private fun calculateInBackground(amount: Int): Disposable =
        Observable.fromCallable { calculate(amount) }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { presenterInterface.showPiProgress() }
            .doOnTerminate { presenterInterface.hidePiProgress() }
            .doOnDispose { presenterInterface.hidePiProgress() }
            .subscribe {

                    presenterInterface.saveResult(it)

                    Log.i(TAG, "resolved")


            }


    private fun calculate(long: Int): Double {

        // speed 1 (medium)
        /* var countInRound = 0

        for (i in 0..long) {

            val randomX = Random().nextFloat()
            val randomY = Random().nextFloat()

            if (Math.sqrt(randomX.toDouble().pow(2.toDouble()) + randomY.toDouble().pow(2.toDouble())) <= 1) {
                countInRound++
            }

        }

        return 4.toDouble() * countInRound / long */
        //speed 2 (fast)
        val sequence = Array<Pair<Float, Float>>(long) { Random().nextFloat() to Random().nextFloat() }

        val ans =
            sequence.filter { Math.sqrt(it.first.toDouble().pow(2.toDouble()) + it.second.toDouble().pow(2.toDouble())) <= 1 }
                .count()

        return 4.toDouble() * ans / long


    }

}

interface PiResolverInterface {

    fun initResolver(mainActivityPresenterInterface: MainActivityPresenterPiResolver)
    fun startResolve(amount: Int)

}
