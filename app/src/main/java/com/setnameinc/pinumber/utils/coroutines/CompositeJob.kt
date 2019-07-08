package com.setnameinc.pinumber.utils.coroutines

import kotlinx.coroutines.Job

class CompositeJob {

    private val map = hashMapOf<String, Job>()

    fun add(job: Job, key: String = job.hashCode().toString()) = map.put(key, job)?.cancel()

    private fun cancel(key: String) = map[key]?.cancel()

    fun cancel() {
        //the .forEach() for maps available since 24 API version
        for (i in map) {

            cancel(i.key)

        }

    }
}