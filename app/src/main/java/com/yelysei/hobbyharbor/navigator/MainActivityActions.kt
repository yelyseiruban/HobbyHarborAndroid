package com.yelysei.hobbyharbor.navigator

import com.yelysei.hobbyharbor.MainActivity

typealias MainActivityAction = (MainActivity) -> Unit
class MainActivityActions {

    var mainActivity: MainActivity? = null
        set(activity) {
            field = activity
            if (activity != null) {
                actions.forEach{ it(activity) }
                actions.clear()
            }
        }

    private val actions = mutableListOf<MainActivityAction>()

    operator fun invoke(action: MainActivityAction) {
        val activity = this.mainActivity
        if (activity == null) {
            actions += action
        } else {
            action(activity)
        }
    }

    fun clear() {
        actions.clear()
    }

}