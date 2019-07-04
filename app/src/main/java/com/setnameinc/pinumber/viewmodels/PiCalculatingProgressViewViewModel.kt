package com.setnameinc.pinumber.viewmodels

import androidx.lifecycle.ViewModel

class PiCalculatingProgressViewViewModel : ViewModel(){

    var setOfCoordinates = mutableSetOf<Pair<Pair<Float, Float>, Boolean>>()
    var isDrawingNow:Boolean = false
    var isDrawn:Boolean = false

}