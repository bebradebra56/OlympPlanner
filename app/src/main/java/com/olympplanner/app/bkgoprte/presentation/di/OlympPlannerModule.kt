package com.olympplanner.app.bkgoprte.presentation.di

import com.olympplanner.app.bkgoprte.data.repo.OlympPlannerRepository
import com.olympplanner.app.bkgoprte.data.shar.OlympPlannerSharedPreference
import com.olympplanner.app.bkgoprte.data.utils.OlympPlannerPushToken
import com.olympplanner.app.bkgoprte.data.utils.OlympPlannerSystemService
import com.olympplanner.app.bkgoprte.domain.usecases.OlympPlannerGetAllUseCase
import com.olympplanner.app.bkgoprte.presentation.pushhandler.OlympPlannerPushHandler
import com.olympplanner.app.bkgoprte.presentation.ui.load.OlympPlannerLoadViewModel
import com.olympplanner.app.bkgoprte.presentation.ui.view.OlympPlannerViFun
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val olympPlannerModule = module {
    factory {
        OlympPlannerPushHandler()
    }
    single {
        OlympPlannerRepository()
    }
    single {
        OlympPlannerSharedPreference(get())
    }
    factory {
        OlympPlannerPushToken()
    }
    factory {
        OlympPlannerSystemService(get())
    }
    factory {
        OlympPlannerGetAllUseCase(
            get(), get(), get()
        )
    }
    factory {
        OlympPlannerViFun(get())
    }
    viewModel {
        OlympPlannerLoadViewModel(get(), get(), get())
    }
}