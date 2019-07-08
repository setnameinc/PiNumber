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
import kotlin.math.sqrt
import kotlin.random.Random

class PiResolver @Inject constructor() : PiResolverInterface {

    private val TAG = this::class.java.simpleName

    private val random = Random

    private lateinit var presenterInterface: MainActivityPresenterPiResolver

    private val disposableBag = CompositeDisposable()

/*
    private val compositeJob = CompositeJob()
*/

    override fun initResolver(mainActivityPresenterInterface: MainActivityPresenterPiResolver) {

        Log.i(TAG, "init presenter")

        presenterInterface = mainActivityPresenterInterface

    }

    override fun startResolve(amount: Long) {

        disposableBag.clear()
        disposableBag.add(calculateInBackground(amount))

        /*compositeJob.add(calculateIt(amount))*/

    }

    /*private fun calculateIt(amount: Int):Job =
        CoroutineScope(Dispatchers.Main).launch {

            withContext(Dispatchers.Main) {
                presenterInterface.showProgress()
            }

            withContext(Dispatchers.IO){

                presenterInterface.saveResult(calculate(amount))

            }

            withContext(Dispatchers.Main){

                presenterInterface.hideProgress()

            }

        }*/

    private fun calculateInBackground(amount: Long): Disposable =
        Observable.fromCallable { calculate(amount) }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { presenterInterface.showProgress() }
            .doOnTerminate { presenterInterface.hideProgress() }
            .doOnDispose { presenterInterface.hideProgress() }
            .subscribe {

                presenterInterface.saveResult(it)

                Log.i(TAG, "resolved")


            }


    private fun calculate(long: Long): Double {

        // speed 1 (high speed)
        var countInRound = 0
        for (i in 0..long) {

            val randomX = random.nextFloat()
            val randomY = random.nextFloat()

            if (sqrt(randomX * randomX + randomY * randomY) <= 1) {

                countInRound++

            }

        }

        //speed 3 (very slow)
        /*val sequence = Array<Pair<Float, Float>>(int) { Random().nextFloat() to Random().nextFloat() }

        val countInRound =
            sequence.filter { Math.sqrt(it.first.toDouble().pow(2.toDouble()) + it.second.toDouble().pow(2.toDouble())) <= 1 }
                .count() */

        return 4.toDouble() * countInRound / long


    }

}

interface PiResolverInterface {

    fun initResolver(mainActivityPresenterInterface: MainActivityPresenterPiResolver)
    fun startResolve(amount: Long)

}
