package com.setnameinc.pinumber.utils.rxutils

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import io.reactivex.subjects.PublishSubject

class RxSearchObservable {

    companion object {

        fun fromView(editText: EditText): PublishSubject<String> {

            val subject = PublishSubject.create<String>()

            editText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {

                    subject.onNext(s.toString())

                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }
            })

            return subject

        }

    }

}