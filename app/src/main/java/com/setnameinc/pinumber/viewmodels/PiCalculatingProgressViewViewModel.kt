package com.setnameinc.pinumber.viewmodels

import androidx.lifecycle.ViewModel
import com.setnameinc.pinumber.customviews.CompositeJob

class PiCalculatingProgressViewViewModel : ViewModel(){

    var setOfCoordinates = mutableSetOf<Pair<Pair<Float, Float>, Boolean>>()
    var isDrawingNow:Boolean = false
    var isDrawn:Boolean = false

    val compositeJob = CompositeJob()

}