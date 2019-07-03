package com.setnameinc.pinumber.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.setnameinc.pinumber.utils.constants.ViewModel.RESULT_DEFAULT_VALUE
import javax.inject.Inject

class ViewModel @Inject constructor() : ViewModel(){

    val amount:MutableLiveData<Long> = MutableLiveData()
    var result:Double = RESULT_DEFAULT_VALUE

}