package com.olympplanner.app.bkgoprte.data.utils

import android.util.Log
import com.olympplanner.app.bkgoprte.presentation.app.OlympPlannerApplication
import com.google.firebase.messaging.FirebaseMessaging
import java.lang.Exception
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class OlympPlannerPushToken {

    suspend fun olympPlannerGetToken(): String = suspendCoroutine { continuation ->
        try {
            FirebaseMessaging.getInstance().token.addOnCompleteListener {
                if (!it.isSuccessful) {
                    continuation.resume(it.result)
                    Log.d(OlympPlannerApplication.OLYMP_PLANNER_MAIN_TAG, "Token error: ${it.exception}")
                } else {
                    continuation.resume(it.result)
                }
            }
        } catch (e: Exception) {
            Log.d(OlympPlannerApplication.OLYMP_PLANNER_MAIN_TAG, "FirebaseMessagingPushToken = null")
            continuation.resume("")
        }
    }


}