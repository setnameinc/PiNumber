package com.setnameinc.pinumber.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class ViewModel @Inject constructor() : ViewModel() {

    var amount: Long = 0
    var result = MutableLiveData<Double>()

    var switchBeautyMod: Boolean = false
    var progressIsVisible = MutableLiveData<Boolean>()

}