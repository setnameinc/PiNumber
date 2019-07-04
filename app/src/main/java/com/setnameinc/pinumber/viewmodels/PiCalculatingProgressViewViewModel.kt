package com.setnameinc.pinumber.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PiCalculatingProgressViewViewModel : ViewModel(){

    /*private val coroutineScope = CoroutineScope(Dispatchers.Main)*/

    var setOfCoordinates = mutableSetOf<Pair<Pair<Float, Float>, Boolean>>()
    var isAvailableForDrawing:Boolean = false

}