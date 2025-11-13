package com.olympplanner.app.bkgoprte.presentation.pushhandler

import android.os.Bundle
import android.util.Log
import com.olympplanner.app.bkgoprte.presentation.app.OlympPlannerApplication

class OlympPlannerPushHandler {
    fun olympPlannerHandlePush(extras: Bundle?) {
        Log.d(OlympPlannerApplication.OLYMP_PLANNER_MAIN_TAG, "Extras from Push = ${extras?.keySet()}")
        if (extras != null) {
            val map = olympPlannerBundleToMap(extras)
            Log.d(OlympPlannerApplication.OLYMP_PLANNER_MAIN_TAG, "Map from Push = $map")
            map?.let {
                if (map.containsKey("url")) {
                    OlympPlannerApplication.OLYMP_PLANNER_FB_LI = map["url"]
                    Log.d(OlympPlannerApplication.OLYMP_PLANNER_MAIN_TAG, "UrlFromActivity = $map")
                }
            }
        } else {
            Log.d(OlympPlannerApplication.OLYMP_PLANNER_MAIN_TAG, "Push data no!")
        }
    }

    private fun olympPlannerBundleToMap(extras: Bundle): Map<String, String?>? {
        val map: MutableMap<String, String?> = HashMap()
        val ks = extras.keySet()
        val iterator: Iterator<String> = ks.iterator()
        while (iterator.hasNext()) {
            val key = iterator.next()
            map[key] = extras.getString(key)
        }
        return map
    }

}