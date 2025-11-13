package com.olympplanner.app.bkgoprte.domain.usecases

import android.util.Log
import com.olympplanner.app.bkgoprte.data.repo.OlympPlannerRepository
import com.olympplanner.app.bkgoprte.data.utils.OlympPlannerPushToken
import com.olympplanner.app.bkgoprte.data.utils.OlympPlannerSystemService
import com.olympplanner.app.bkgoprte.domain.model.OlympPlannerEntity
import com.olympplanner.app.bkgoprte.domain.model.OlympPlannerParam
import com.olympplanner.app.bkgoprte.presentation.app.OlympPlannerApplication

class OlympPlannerGetAllUseCase(
    private val olympPlannerRepository: OlympPlannerRepository,
    private val olympPlannerSystemService: OlympPlannerSystemService,
    private val olympPlannerPushToken: OlympPlannerPushToken,
) {
    suspend operator fun invoke(conversion: MutableMap<String, Any>?) : OlympPlannerEntity?{
        val params = OlympPlannerParam(
            olympPlannerLocale = olympPlannerSystemService.olympPlannerGetLocale(),
            olympPlannerPushToken = olympPlannerPushToken.olympPlannerGetToken(),
            olympPlannerAfId = olympPlannerSystemService.olympPlannerGetAppsflyerId()
        )
        Log.d(OlympPlannerApplication.OLYMP_PLANNER_MAIN_TAG, "Params for request: $params")
        return olympPlannerRepository.olympPlannerGetClient(params, conversion)
    }



}