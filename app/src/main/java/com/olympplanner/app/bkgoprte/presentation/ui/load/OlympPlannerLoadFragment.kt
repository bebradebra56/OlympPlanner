package com.olympplanner.app.bkgoprte.presentation.ui.load

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.olympplanner.app.MainActivity
import com.olympplanner.app.R
import com.olympplanner.app.bkgoprte.data.shar.OlympPlannerSharedPreference
import com.olympplanner.app.databinding.FragmentLoadOlympPlannerBinding
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class OlympPlannerLoadFragment : Fragment(R.layout.fragment_load_olymp_planner) {
    private lateinit var olympPlannerLoadBinding: FragmentLoadOlympPlannerBinding

    private val olympPlannerLoadViewModel by viewModel<OlympPlannerLoadViewModel>()

    private val olympPlannerSharedPreference by inject<OlympPlannerSharedPreference>()

    private var olympPlannerUrl = ""

    private val olympPlannerRequestNotificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            olympPlannerNavigateToSuccess(olympPlannerUrl)
        } else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                olympPlannerSharedPreference.olympPlannerNotificationRequest =
                    (System.currentTimeMillis() / 1000) + 259200
                olympPlannerNavigateToSuccess(olympPlannerUrl)
            } else {
                olympPlannerNavigateToSuccess(olympPlannerUrl)
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        olympPlannerLoadBinding = FragmentLoadOlympPlannerBinding.bind(view)

        olympPlannerLoadBinding.olympPlannerGrandButton.setOnClickListener {
            val olympPlannerPermission = Manifest.permission.POST_NOTIFICATIONS
            olympPlannerRequestNotificationPermission.launch(olympPlannerPermission)
            olympPlannerSharedPreference.olympPlannerNotificationRequestedBefore = true
        }

        olympPlannerLoadBinding.olympPlannerSkipButton.setOnClickListener {
            olympPlannerSharedPreference.olympPlannerNotificationRequest =
                (System.currentTimeMillis() / 1000) + 259200
            olympPlannerNavigateToSuccess(olympPlannerUrl)
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                olympPlannerLoadViewModel.olympPlannerHomeScreenState.collect {
                    when (it) {
                        is OlympPlannerLoadViewModel.OlympPlannerHomeScreenState.OlympPlannerLoading -> {

                        }

                        is OlympPlannerLoadViewModel.OlympPlannerHomeScreenState.OlympPlannerError -> {
                            requireActivity().startActivity(
                                Intent(
                                    requireContext(),
                                    MainActivity::class.java
                                )
                            )
                            requireActivity().finish()
                        }

                        is OlympPlannerLoadViewModel.OlympPlannerHomeScreenState.OlympPlannerSuccess -> {
                            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) {
                                val olympPlannerPermission = Manifest.permission.POST_NOTIFICATIONS
                                val olympPlannerPermissionRequestedBefore = olympPlannerSharedPreference.olympPlannerNotificationRequestedBefore

                                if (ContextCompat.checkSelfPermission(requireContext(), olympPlannerPermission) == PackageManager.PERMISSION_GRANTED) {
                                    olympPlannerNavigateToSuccess(it.data)
                                } else if (!olympPlannerPermissionRequestedBefore && (System.currentTimeMillis() / 1000 > olympPlannerSharedPreference.olympPlannerNotificationRequest)) {
                                    // первый раз — показываем UI для запроса
                                    olympPlannerLoadBinding.olympPlannerNotiGroup.visibility = View.VISIBLE
                                    olympPlannerLoadBinding.olympPlannerLoadingGroup.visibility = View.GONE
                                    olympPlannerUrl = it.data
                                } else if (shouldShowRequestPermissionRationale(olympPlannerPermission)) {
                                    // временный отказ — через 3 дня можно показать
                                    if (System.currentTimeMillis() / 1000 > olympPlannerSharedPreference.olympPlannerNotificationRequest) {
                                        olympPlannerLoadBinding.olympPlannerNotiGroup.visibility = View.VISIBLE
                                        olympPlannerLoadBinding.olympPlannerLoadingGroup.visibility = View.GONE
                                        olympPlannerUrl = it.data
                                    } else {
                                        olympPlannerNavigateToSuccess(it.data)
                                    }
                                } else {
                                    // навсегда отклонено — просто пропускаем
                                    olympPlannerNavigateToSuccess(it.data)
                                }
                            } else {
                                olympPlannerNavigateToSuccess(it.data)
                            }
                        }

                        OlympPlannerLoadViewModel.OlympPlannerHomeScreenState.OlympPlannerNotInternet -> {
                            olympPlannerLoadBinding.olympPlannerStateGroup.visibility = View.VISIBLE
                            olympPlannerLoadBinding.olympPlannerLoadingGroup.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }


    private fun olympPlannerNavigateToSuccess(data: String) {
        findNavController().navigate(
            R.id.action_olympPlannerLoadFragment_to_olympPlannerV,
            bundleOf(OLYMP_PLANNER_D to data)
        )
    }

    companion object {
        const val OLYMP_PLANNER_D = "olympPlannerData"
    }
}