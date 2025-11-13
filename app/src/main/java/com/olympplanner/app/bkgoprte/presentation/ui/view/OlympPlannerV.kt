package com.olympplanner.app.bkgoprte.presentation.ui.view

import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.olympplanner.app.bkgoprte.presentation.app.OlympPlannerApplication
import com.olympplanner.app.bkgoprte.presentation.ui.load.OlympPlannerLoadFragment
import org.koin.android.ext.android.inject

class OlympPlannerV : Fragment(){

    private lateinit var olympPlannerPhoto: Uri
    private var olympPlannerFilePathFromChrome: ValueCallback<Array<Uri>>? = null

    private val olympPlannerTakeFile: ActivityResultLauncher<PickVisualMediaRequest> = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        olympPlannerFilePathFromChrome?.onReceiveValue(arrayOf(it ?: Uri.EMPTY))
        olympPlannerFilePathFromChrome = null
    }

    private val olympPlannerTakePhoto: ActivityResultLauncher<Uri> = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            olympPlannerFilePathFromChrome?.onReceiveValue(arrayOf(olympPlannerPhoto))
            olympPlannerFilePathFromChrome = null
        } else {
            olympPlannerFilePathFromChrome?.onReceiveValue(null)
            olympPlannerFilePathFromChrome = null
        }
    }

    private val olympPlannerDataStore by activityViewModels<OlympPlannerDataStore>()


    private val olympPlannerViFun by inject<OlympPlannerViFun>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(OlympPlannerApplication.OLYMP_PLANNER_MAIN_TAG, "Fragment onCreate")
        CookieManager.getInstance().setAcceptCookie(true)
        requireActivity().onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (olympPlannerDataStore.olympPlannerView.canGoBack()) {
                        olympPlannerDataStore.olympPlannerView.goBack()
                        Log.d(OlympPlannerApplication.OLYMP_PLANNER_MAIN_TAG, "WebView can go back")
                    } else if (olympPlannerDataStore.olympPlannerViList.size > 1) {
                        Log.d(OlympPlannerApplication.OLYMP_PLANNER_MAIN_TAG, "WebView can`t go back")
                        olympPlannerDataStore.olympPlannerViList.removeAt(olympPlannerDataStore.olympPlannerViList.lastIndex)
                        Log.d(OlympPlannerApplication.OLYMP_PLANNER_MAIN_TAG, "WebView list size ${olympPlannerDataStore.olympPlannerViList.size}")
                        olympPlannerDataStore.olympPlannerView.destroy()
                        val previousWebView = olympPlannerDataStore.olympPlannerViList.last()
                        olympPlannerAttachWebViewToContainer(previousWebView)
                        olympPlannerDataStore.olympPlannerView = previousWebView
                    }
                }

            })
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (olympPlannerDataStore.olympPlannerIsFirstCreate) {
            olympPlannerDataStore.olympPlannerIsFirstCreate = false
            olympPlannerDataStore.olympPlannerContainerView = FrameLayout(requireContext()).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                id = View.generateViewId()
            }
            return olympPlannerDataStore.olympPlannerContainerView
        } else {
            return olympPlannerDataStore.olympPlannerContainerView
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(OlympPlannerApplication.OLYMP_PLANNER_MAIN_TAG, "onViewCreated")
        if (olympPlannerDataStore.olympPlannerViList.isEmpty()) {
            olympPlannerDataStore.olympPlannerView = OlympPlannerVi(requireContext(), object :
                OlympPlannerCallBack {
                override fun olympPlannerHandleCreateWebWindowRequest(olympPlannerVi: OlympPlannerVi) {
                    olympPlannerDataStore.olympPlannerViList.add(olympPlannerVi)
                    Log.d(OlympPlannerApplication.OLYMP_PLANNER_MAIN_TAG, "WebView list size = ${olympPlannerDataStore.olympPlannerViList.size}")
                    Log.d(OlympPlannerApplication.OLYMP_PLANNER_MAIN_TAG, "CreateWebWindowRequest")
                    olympPlannerDataStore.olympPlannerView = olympPlannerVi
                    olympPlannerVi.olympPlannerSetFileChooserHandler { callback ->
                        olympPlannerHandleFileChooser(callback)
                    }
                    olympPlannerAttachWebViewToContainer(olympPlannerVi)
                }

            }, olympPlannerWindow = requireActivity().window).apply {
                olympPlannerSetFileChooserHandler { callback ->
                    olympPlannerHandleFileChooser(callback)
                }
            }
            olympPlannerDataStore.olympPlannerView.olympPlannerFLoad(arguments?.getString(
                OlympPlannerLoadFragment.OLYMP_PLANNER_D) ?: "")
//            ejvview.fLoad("www.google.com")
            olympPlannerDataStore.olympPlannerViList.add(olympPlannerDataStore.olympPlannerView)
            olympPlannerAttachWebViewToContainer(olympPlannerDataStore.olympPlannerView)
        } else {
            olympPlannerDataStore.olympPlannerViList.forEach { webView ->
                webView.olympPlannerSetFileChooserHandler { callback ->
                    olympPlannerHandleFileChooser(callback)
                }
            }
            olympPlannerDataStore.olympPlannerView = olympPlannerDataStore.olympPlannerViList.last()

            olympPlannerAttachWebViewToContainer(olympPlannerDataStore.olympPlannerView)
        }
        Log.d(OlympPlannerApplication.OLYMP_PLANNER_MAIN_TAG, "WebView list size = ${olympPlannerDataStore.olympPlannerViList.size}")
    }

    private fun olympPlannerHandleFileChooser(callback: ValueCallback<Array<Uri>>?) {
        Log.d(OlympPlannerApplication.OLYMP_PLANNER_MAIN_TAG, "handleFileChooser called, callback: ${callback != null}")

        olympPlannerFilePathFromChrome = callback

        val listItems: Array<out String> = arrayOf("Select from file", "To make a photo")
        val listener = DialogInterface.OnClickListener { _, which ->
            when (which) {
                0 -> {
                    Log.d(OlympPlannerApplication.OLYMP_PLANNER_MAIN_TAG, "Launching file picker")
                    olympPlannerTakeFile.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
                1 -> {
                    Log.d(OlympPlannerApplication.OLYMP_PLANNER_MAIN_TAG, "Launching camera")
                    olympPlannerPhoto = olympPlannerViFun.olympPlannerSavePhoto()
                    olympPlannerTakePhoto.launch(olympPlannerPhoto)
                }
            }
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Choose a method")
            .setItems(listItems, listener)
            .setCancelable(true)
            .setOnCancelListener {
                Log.d(OlympPlannerApplication.OLYMP_PLANNER_MAIN_TAG, "File chooser canceled")
                callback?.onReceiveValue(null)
                olympPlannerFilePathFromChrome = null
            }
            .create()
            .show()
    }

    private fun olympPlannerAttachWebViewToContainer(w: OlympPlannerVi) {
        olympPlannerDataStore.olympPlannerContainerView.post {
            (w.parent as? ViewGroup)?.removeView(w)
            olympPlannerDataStore.olympPlannerContainerView.removeAllViews()
            olympPlannerDataStore.olympPlannerContainerView.addView(w)
        }
    }


}