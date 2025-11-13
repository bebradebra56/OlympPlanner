package com.olympplanner.app

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.olympplanner.app.bkgoprte.OlympPlannerGlobalLayoutUtil
import com.olympplanner.app.bkgoprte.presentation.app.OlympPlannerApplication
import com.olympplanner.app.bkgoprte.presentation.pushhandler.OlympPlannerPushHandler
import com.olympplanner.app.bkgoprte.olympPlannerSetupSystemBars
import org.koin.android.ext.android.inject

class OlympPlannerActivity : AppCompatActivity() {


    private val olympPlannerPushHandler by inject<OlympPlannerPushHandler>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        olympPlannerSetupSystemBars()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContentView(R.layout.activity_olymp_planner)

        val olympPlannerRootView = findViewById<View>(android.R.id.content)
        OlympPlannerGlobalLayoutUtil().olympPlannerAssistActivity(this)
        ViewCompat.setOnApplyWindowInsetsListener(olympPlannerRootView) { olympPlannerView, olympPlannerInsets ->
            val olympPlannerSystemBars = olympPlannerInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            val olympPlannerDisplayCutout = olympPlannerInsets.getInsets(WindowInsetsCompat.Type.displayCutout())
            val olympPlannerIme = olympPlannerInsets.getInsets(WindowInsetsCompat.Type.ime())


            val olympPlannerTopPadding = maxOf(olympPlannerSystemBars.top, olympPlannerDisplayCutout.top)
            val olympPlannerLeftPadding = maxOf(olympPlannerSystemBars.left, olympPlannerDisplayCutout.left)
            val olympPlannerRightPadding = maxOf(olympPlannerSystemBars.right, olympPlannerDisplayCutout.right)
            window.setSoftInputMode(OlympPlannerApplication.olympPlannerInputMode)

            if (window.attributes.softInputMode == WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN) {
                Log.d(OlympPlannerApplication.OLYMP_PLANNER_MAIN_TAG, "ADJUST PUN")
                val olympPlannerBottomInset = maxOf(olympPlannerSystemBars.bottom, olympPlannerDisplayCutout.bottom)

                olympPlannerView.setPadding(olympPlannerLeftPadding, olympPlannerTopPadding, olympPlannerRightPadding, 0)

                olympPlannerView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = olympPlannerBottomInset
                }
            } else {
                Log.d(OlympPlannerApplication.OLYMP_PLANNER_MAIN_TAG, "ADJUST RESIZE")

                val olympPlannerBottomInset = maxOf(olympPlannerSystemBars.bottom, olympPlannerDisplayCutout.bottom, olympPlannerIme.bottom)

                olympPlannerView.setPadding(olympPlannerLeftPadding, olympPlannerTopPadding, olympPlannerRightPadding, 0)

                olympPlannerView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = olympPlannerBottomInset
                }
            }



            WindowInsetsCompat.CONSUMED
        }
        Log.d(OlympPlannerApplication.OLYMP_PLANNER_MAIN_TAG, "Activity onCreate()")
        olympPlannerPushHandler.olympPlannerHandlePush(intent.extras)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            olympPlannerSetupSystemBars()
        }
    }

    override fun onResume() {
        super.onResume()
        olympPlannerSetupSystemBars()
    }
}