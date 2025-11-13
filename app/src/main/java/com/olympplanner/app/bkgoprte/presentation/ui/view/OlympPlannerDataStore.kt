package com.olympplanner.app.bkgoprte.presentation.ui.view

import android.annotation.SuppressLint
import android.widget.FrameLayout
import androidx.lifecycle.ViewModel

class OlympPlannerDataStore : ViewModel(){
    val olympPlannerViList: MutableList<OlympPlannerVi> = mutableListOf()
    var olympPlannerIsFirstCreate = true
    @SuppressLint("StaticFieldLeak")
    lateinit var olympPlannerContainerView: FrameLayout
    @SuppressLint("StaticFieldLeak")
    lateinit var olympPlannerView: OlympPlannerVi

}