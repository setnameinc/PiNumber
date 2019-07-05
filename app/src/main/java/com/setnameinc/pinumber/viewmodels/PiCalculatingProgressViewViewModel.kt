package com.setnameinc.pinumber.viewmodels

import android.content.res.Configuration
import androidx.lifecycle.ViewModel
import com.setnameinc.pinumber.utils.coroutines.CompositeJob

class PiCalculatingProgressViewViewModel : ViewModel(){

    //using to restore and recalculate value after rotation
    var setOfCoordinates = hashSetOf<Pair<Pair<Float, Float>, Boolean>>()

    //using to detect piView state
    var isDrawingNow:Boolean = false

    //it is using for storing all coroutines links
    val compositeJob = CompositeJob()

    //using for control items generator
    var isSetMax = false

}