package com.example.fable.util

import androidx.test.espresso.IdlingResource
import androidx.test.runner.lifecycle.ActivityLifecycleMonitor
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import kotlin.concurrent.Volatile

class WaitActivityIsResumedIdlingResource(private val activityToWaitClassName: String) :
    IdlingResource {
    private val instance: ActivityLifecycleMonitor = ActivityLifecycleMonitorRegistry.getInstance()

    @Volatile
    private var resourceCallback: IdlingResource.ResourceCallback? = null
    private var resumed: Boolean = false

    override fun getName(): String {
        return javaClass.name
    }

    override fun isIdleNow(): Boolean {
        resumed = isActivityLaunched
        if (resumed && resourceCallback != null) {
            resourceCallback!!.onTransitionToIdle()
        }

        return resumed
    }

    private val isActivityLaunched: Boolean
        get() {
            val activitiesInStage = instance.getActivitiesInStage(Stage.RESUMED)
            for (activity in activitiesInStage) {
                if (activity.javaClass.name == activityToWaitClassName) {
                    return true
                }
            }
            return false
        }

    override fun registerIdleTransitionCallback(resourceCallback: IdlingResource.ResourceCallback) {
        this.resourceCallback = resourceCallback
    }
}