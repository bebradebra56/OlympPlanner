package com.olympplanner.app.bkgoprte.data.shar

import android.content.Context
import androidx.core.content.edit

class OlympPlannerSharedPreference(context: Context) {
    private val olympPlannerPrefs = context.getSharedPreferences("olympPlannerSharedPrefsAb", Context.MODE_PRIVATE)

    var olympPlannerSavedUrl: String
        get() = olympPlannerPrefs.getString(OLYMP_PLANNER_SAVED_URL, "") ?: ""
        set(value) = olympPlannerPrefs.edit { putString(OLYMP_PLANNER_SAVED_URL, value) }

    var olympPlannerExpired : Long
        get() = olympPlannerPrefs.getLong(OLYMP_PLANNER_EXPIRED, 0L)
        set(value) = olympPlannerPrefs.edit { putLong(OLYMP_PLANNER_EXPIRED, value) }

    var olympPlannerAppState: Int
        get() = olympPlannerPrefs.getInt(OLYMP_PLANNER_APPLICATION_STATE, 0)
        set(value) = olympPlannerPrefs.edit { putInt(OLYMP_PLANNER_APPLICATION_STATE, value) }

    var olympPlannerNotificationRequest: Long
        get() = olympPlannerPrefs.getLong(OLYMP_PLANNER_NOTIFICAITON_REQUEST, 0L)
        set(value) = olympPlannerPrefs.edit { putLong(OLYMP_PLANNER_NOTIFICAITON_REQUEST, value) }

    var olympPlannerNotificationRequestedBefore: Boolean
        get() = olympPlannerPrefs.getBoolean(OLYMP_PLANNER_NOTIFICATION_REQUEST_BEFORE, false)
        set(value) = olympPlannerPrefs.edit { putBoolean(
            OLYMP_PLANNER_NOTIFICATION_REQUEST_BEFORE, value) }

    companion object {
        private const val OLYMP_PLANNER_SAVED_URL = "olympPlannerSavedUrl"
        private const val OLYMP_PLANNER_EXPIRED = "olympPlannerExpired"
        private const val OLYMP_PLANNER_APPLICATION_STATE = "olympPlannerApplicationState"
        private const val OLYMP_PLANNER_NOTIFICAITON_REQUEST = "olympPlannerNotificationRequest"
        private const val OLYMP_PLANNER_NOTIFICATION_REQUEST_BEFORE = "olympPlannerNotificationRequestedBefore"
    }
}