package com.olympplanner.app.bkgoprte.domain.model

import com.google.gson.annotations.SerializedName


private const val OLYMP_PLANNER_A = "com.olympplanner.app"
private const val OLYMP_PLANNER_B = "olympplanner"
data class OlympPlannerParam (
    @SerializedName("af_id")
    val olympPlannerAfId: String,
    @SerializedName("bundle_id")
    val olympPlannerBundleId: String = OLYMP_PLANNER_A,
    @SerializedName("os")
    val olympPlannerOs: String = "Android",
    @SerializedName("store_id")
    val olympPlannerStoreId: String = OLYMP_PLANNER_A,
    @SerializedName("locale")
    val olympPlannerLocale: String,
    @SerializedName("push_token")
    val olympPlannerPushToken: String,
    @SerializedName("firebase_project_id")
    val olympPlannerFirebaseProjectId: String = OLYMP_PLANNER_B,

    )