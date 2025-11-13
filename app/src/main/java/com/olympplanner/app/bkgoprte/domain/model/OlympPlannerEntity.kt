package com.olympplanner.app.bkgoprte.domain.model

import com.google.gson.annotations.SerializedName


data class OlympPlannerEntity (
    @SerializedName("ok")
    val olympPlannerOk: String,
    @SerializedName("url")
    val olympPlannerUrl: String,
    @SerializedName("expires")
    val olympPlannerExpires: Long,
)