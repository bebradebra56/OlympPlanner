package com.olympplanner.app.bkgoprte

import android.app.Activity
import android.graphics.Rect
import android.view.View
import android.widget.FrameLayout
import com.olympplanner.app.bkgoprte.presentation.app.OlympPlannerApplication

class OlympPlannerGlobalLayoutUtil {

    private var olympPlannerMChildOfContent: View? = null
    private var olympPlannerUsableHeightPrevious = 0

    fun olympPlannerAssistActivity(activity: Activity) {
        val content = activity.findViewById<FrameLayout>(android.R.id.content)
        olympPlannerMChildOfContent = content.getChildAt(0)

        olympPlannerMChildOfContent?.viewTreeObserver?.addOnGlobalLayoutListener {
            possiblyResizeChildOfContent(activity)
        }
    }

    private fun possiblyResizeChildOfContent(activity: Activity) {
        val olympPlannerUsableHeightNow = olympPlannerComputeUsableHeight()
        if (olympPlannerUsableHeightNow != olympPlannerUsableHeightPrevious) {
            val olympPlannerUsableHeightSansKeyboard = olympPlannerMChildOfContent?.rootView?.height ?: 0
            val olympPlannerHeightDifference = olympPlannerUsableHeightSansKeyboard - olympPlannerUsableHeightNow

            if (olympPlannerHeightDifference > (olympPlannerUsableHeightSansKeyboard / 4)) {
                activity.window.setSoftInputMode(OlympPlannerApplication.olympPlannerInputMode)
            } else {
                activity.window.setSoftInputMode(OlympPlannerApplication.olympPlannerInputMode)
            }
//            mChildOfContent?.requestLayout()
            olympPlannerUsableHeightPrevious = olympPlannerUsableHeightNow
        }
    }

    private fun olympPlannerComputeUsableHeight(): Int {
        val r = Rect()
        olympPlannerMChildOfContent?.getWindowVisibleDisplayFrame(r)
        return r.bottom - r.top  // Visible height без status bar
    }
}