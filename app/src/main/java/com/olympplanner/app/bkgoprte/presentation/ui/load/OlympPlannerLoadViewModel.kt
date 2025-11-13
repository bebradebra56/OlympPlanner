package com.olympplanner.app.bkgoprte.presentation.ui.load

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.olympplanner.app.bkgoprte.data.shar.OlympPlannerSharedPreference
import com.olympplanner.app.bkgoprte.data.utils.OlympPlannerSystemService
import com.olympplanner.app.bkgoprte.domain.usecases.OlympPlannerGetAllUseCase
import com.olympplanner.app.bkgoprte.presentation.app.OlympPlannerAppsFlyerState
import com.olympplanner.app.bkgoprte.presentation.app.OlympPlannerApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OlympPlannerLoadViewModel(
    private val olympPlannerGetAllUseCase: OlympPlannerGetAllUseCase,
    private val olympPlannerSharedPreference: OlympPlannerSharedPreference,
    private val olympPlannerSystemService: OlympPlannerSystemService
) : ViewModel() {

    private val _olympPlannerHomeScreenState: MutableStateFlow<OlympPlannerHomeScreenState> =
        MutableStateFlow(OlympPlannerHomeScreenState.OlympPlannerLoading)
    val olympPlannerHomeScreenState = _olympPlannerHomeScreenState.asStateFlow()

    private var olympPlannerGetApps = false


    init {
        viewModelScope.launch {
            when (olympPlannerSharedPreference.olympPlannerAppState) {
                0 -> {
                    if (olympPlannerSystemService.olympPlannerIsOnline()) {
                        OlympPlannerApplication.olympPlannerConversionFlow.collect {
                            when(it) {
                                OlympPlannerAppsFlyerState.OlympPlannerDefault -> {}
                                OlympPlannerAppsFlyerState.OlympPlannerError -> {
                                    olympPlannerSharedPreference.olympPlannerAppState = 2
                                    _olympPlannerHomeScreenState.value =
                                        OlympPlannerHomeScreenState.OlympPlannerError
                                    olympPlannerGetApps = true
                                }
                                is OlympPlannerAppsFlyerState.OlympPlannerSuccess -> {
                                    if (!olympPlannerGetApps) {
                                        olympPlannerGetData(it.olympPlannerData)
                                        olympPlannerGetApps = true
                                    }
                                }
                            }
                        }
                    } else {
                        _olympPlannerHomeScreenState.value =
                            OlympPlannerHomeScreenState.OlympPlannerNotInternet
                    }
                }
                1 -> {
                    if (olympPlannerSystemService.olympPlannerIsOnline()) {
                        if (OlympPlannerApplication.OLYMP_PLANNER_FB_LI != null) {
                            _olympPlannerHomeScreenState.value =
                                OlympPlannerHomeScreenState.OlympPlannerSuccess(
                                    OlympPlannerApplication.OLYMP_PLANNER_FB_LI.toString()
                                )
                        } else if (System.currentTimeMillis() / 1000 > olympPlannerSharedPreference.olympPlannerExpired) {
                            Log.d(OlympPlannerApplication.OLYMP_PLANNER_MAIN_TAG, "Current time more then expired, repeat request")
                            OlympPlannerApplication.olympPlannerConversionFlow.collect {
                                when(it) {
                                    OlympPlannerAppsFlyerState.OlympPlannerDefault -> {}
                                    OlympPlannerAppsFlyerState.OlympPlannerError -> {
                                        _olympPlannerHomeScreenState.value =
                                            OlympPlannerHomeScreenState.OlympPlannerSuccess(
                                                olympPlannerSharedPreference.olympPlannerSavedUrl
                                            )
                                        olympPlannerGetApps = true
                                    }
                                    is OlympPlannerAppsFlyerState.OlympPlannerSuccess -> {
                                        if (!olympPlannerGetApps) {
                                            olympPlannerGetData(it.olympPlannerData)
                                            olympPlannerGetApps = true
                                        }
                                    }
                                }
                            }
                        } else {
                            Log.d(OlympPlannerApplication.OLYMP_PLANNER_MAIN_TAG, "Current time less then expired, use saved url")
                            _olympPlannerHomeScreenState.value =
                                OlympPlannerHomeScreenState.OlympPlannerSuccess(
                                    olympPlannerSharedPreference.olympPlannerSavedUrl
                                )
                        }
                    } else {
                        _olympPlannerHomeScreenState.value =
                            OlympPlannerHomeScreenState.OlympPlannerNotInternet
                    }
                }
                2 -> {
                    _olympPlannerHomeScreenState.value =
                        OlympPlannerHomeScreenState.OlympPlannerError
                }
            }
        }
    }


    private suspend fun olympPlannerGetData(conversation: MutableMap<String, Any>?) {
        val olympPlannerData = olympPlannerGetAllUseCase.invoke(conversation)
        if (olympPlannerSharedPreference.olympPlannerAppState == 0) {
            if (olympPlannerData == null) {
                olympPlannerSharedPreference.olympPlannerAppState = 2
                _olympPlannerHomeScreenState.value =
                    OlympPlannerHomeScreenState.OlympPlannerError
            } else {
                olympPlannerSharedPreference.olympPlannerAppState = 1
                olympPlannerSharedPreference.apply {
                    olympPlannerExpired = olympPlannerData.olympPlannerExpires
                    olympPlannerSavedUrl = olympPlannerData.olympPlannerUrl
                }
                _olympPlannerHomeScreenState.value =
                    OlympPlannerHomeScreenState.OlympPlannerSuccess(olympPlannerData.olympPlannerUrl)
            }
        } else  {
            if (olympPlannerData == null) {
                _olympPlannerHomeScreenState.value =
                    OlympPlannerHomeScreenState.OlympPlannerSuccess(olympPlannerSharedPreference.olympPlannerSavedUrl)
            } else {
                olympPlannerSharedPreference.apply {
                    olympPlannerExpired = olympPlannerData.olympPlannerExpires
                    olympPlannerSavedUrl = olympPlannerData.olympPlannerUrl
                }
                _olympPlannerHomeScreenState.value =
                    OlympPlannerHomeScreenState.OlympPlannerSuccess(olympPlannerData.olympPlannerUrl)
            }
        }
    }


    sealed class OlympPlannerHomeScreenState {
        data object OlympPlannerLoading : OlympPlannerHomeScreenState()
        data object OlympPlannerError : OlympPlannerHomeScreenState()
        data class OlympPlannerSuccess(val data: String) : OlympPlannerHomeScreenState()
        data object OlympPlannerNotInternet: OlympPlannerHomeScreenState()
    }
}