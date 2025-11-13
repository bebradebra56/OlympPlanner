package com.olympplanner.app.bkgoprte.presentation.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.WindowManager
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.appsflyer.attribution.AppsFlyerRequestListener
import com.appsflyer.deeplink.DeepLink
import com.appsflyer.deeplink.DeepLinkListener
import com.appsflyer.deeplink.DeepLinkResult
import com.olympplanner.app.R
import com.olympplanner.app.bkgoprte.presentation.di.olympPlannerModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query


sealed interface OlympPlannerAppsFlyerState {
    data object OlympPlannerDefault : OlympPlannerAppsFlyerState
    data class OlympPlannerSuccess(val olympPlannerData: MutableMap<String, Any>?) :
        OlympPlannerAppsFlyerState

    data object OlympPlannerError : OlympPlannerAppsFlyerState
}

interface OlympPlannerAppsApi {
    @Headers("Content-Type: application/json")
    @GET(OLYMP_PLANNER_LIN)
    fun olympPlannerGetClient(
        @Query("devkey") devkey: String,
        @Query("device_id") deviceId: String,
    ): Call<MutableMap<String, Any>?>
}

private const val OLYMP_PLANNER_APP_DEV = "WwjSTFQvYLmVNq5Xpw4xKK"
private const val OLYMP_PLANNER_LIN = "com.olympplanner.app"

class OlympPlannerApplication : Application() {
    private var olympPlannerIsResumed = false
    private var olympPlannerConversionTimeoutJob: Job? = null
    private var olympPlannerDeepLinkData: MutableMap<String, Any>? = null

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()
        val appsflyer = AppsFlyerLib.getInstance()
        olympPlannerSetDebufLogger(appsflyer)
        olympPlannerMinTimeBetween(appsflyer)

        AppsFlyerLib.getInstance().subscribeForDeepLink(object : DeepLinkListener {
            override fun onDeepLinking(p0: DeepLinkResult) {
                when (p0.status) {
                    DeepLinkResult.Status.FOUND -> {
                        olympPlannerExtractDeepMap(p0.deepLink)
                        Log.d(OLYMP_PLANNER_MAIN_TAG, "onDeepLinking found: ${p0.deepLink}")

                    }

                    DeepLinkResult.Status.NOT_FOUND -> {
                        Log.d(OLYMP_PLANNER_MAIN_TAG, "onDeepLinking not found: ${p0.deepLink}")
                    }

                    DeepLinkResult.Status.ERROR -> {
                        Log.d(OLYMP_PLANNER_MAIN_TAG, "onDeepLinking error: ${p0.error}")
                    }
                }
            }

        })


        appsflyer.init(
            OLYMP_PLANNER_APP_DEV,
            object : AppsFlyerConversionListener {
                override fun onConversionDataSuccess(p0: MutableMap<String, Any>?) {
                    olympPlannerConversionTimeoutJob?.cancel()
                    Log.d(OLYMP_PLANNER_MAIN_TAG, "onConversionDataSuccess: $p0")

                    val afStatus = p0?.get("af_status")?.toString() ?: "null"
                    if (afStatus == "Organic") {
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                delay(5000)
                                val api = olympPlannerGetApi(
                                    "https://gcdsdk.appsflyer.com/install_data/v4.0/",
                                    null
                                )
                                val response = api.olympPlannerGetClient(
                                    devkey = OLYMP_PLANNER_APP_DEV,
                                    deviceId = olympPlannerGetAppsflyerId()
                                ).awaitResponse()

                                val resp = response.body()
                                Log.d(OLYMP_PLANNER_MAIN_TAG, "After 5s: $resp")
                                if (resp?.get("af_status") == "Organic" || resp?.get("af_status") == null) {
                                    olympPlannerResume(OlympPlannerAppsFlyerState.OlympPlannerError)
                                } else {
                                    olympPlannerResume(
                                        OlympPlannerAppsFlyerState.OlympPlannerSuccess(resp)
                                    )
                                }
                            } catch (d: Exception) {
                                Log.d(OLYMP_PLANNER_MAIN_TAG, "Error: ${d.message}")
                                olympPlannerResume(OlympPlannerAppsFlyerState.OlympPlannerError)
                            }
                        }
                    } else {
                        olympPlannerResume(OlympPlannerAppsFlyerState.OlympPlannerSuccess(p0))
                    }
                }

                override fun onConversionDataFail(p0: String?) {
                    olympPlannerConversionTimeoutJob?.cancel()
                    Log.d(OLYMP_PLANNER_MAIN_TAG, "onConversionDataFail: $p0")
                    olympPlannerResume(OlympPlannerAppsFlyerState.OlympPlannerError)
                }

                override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {
                    Log.d(OLYMP_PLANNER_MAIN_TAG, "onAppOpenAttribution")
                }

                override fun onAttributionFailure(p0: String?) {
                    Log.d(OLYMP_PLANNER_MAIN_TAG, "onAttributionFailure: $p0")
                }
            },
            this
        )

        appsflyer.start(this, OLYMP_PLANNER_APP_DEV, object :
            AppsFlyerRequestListener {
            override fun onSuccess() {
                Log.d(OLYMP_PLANNER_MAIN_TAG, "AppsFlyer started")
            }

            override fun onError(p0: Int, p1: String) {
                Log.d(OLYMP_PLANNER_MAIN_TAG, "AppsFlyer start error: $p0 - $p1")
            }
        })
        olympPlannerStartConversionTimeout()
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@OlympPlannerApplication)
            modules(
                listOf(
                    olympPlannerModule
                )
            )
        }
    }

    private fun olympPlannerExtractDeepMap(dl: DeepLink) {
        val map = mutableMapOf<String, Any>()
        dl.deepLinkValue?.let { map["deep_link_value"] = it }
        dl.mediaSource?.let { map["media_source"] = it }
        dl.campaign?.let { map["campaign"] = it }
        dl.campaignId?.let { map["campaign_id"] = it }
        dl.afSub1?.let { map["af_sub1"] = it }
        dl.afSub2?.let { map["af_sub2"] = it }
        dl.afSub3?.let { map["af_sub3"] = it }
        dl.afSub4?.let { map["af_sub4"] = it }
        dl.afSub5?.let { map["af_sub5"] = it }
        dl.matchType?.let { map["match_type"] = it }
        dl.clickHttpReferrer?.let { map["click_http_referrer"] = it }
        dl.getStringValue("timestamp")?.let { map["timestamp"] = it }
        dl.isDeferred?.let { map["is_deferred"] = it }
        for (i in 1..10) {
            val key = "deep_link_sub$i"
            dl.getStringValue(key)?.let {
                if (!map.containsKey(key)) {
                    map[key] = it
                }
            }
        }
        Log.d(OLYMP_PLANNER_MAIN_TAG, "Extracted DeepLink data: $map")
        olympPlannerDeepLinkData = map
    }

    private fun olympPlannerStartConversionTimeout() {
        olympPlannerConversionTimeoutJob = CoroutineScope(Dispatchers.Main).launch {
            delay(30000)
            if (!olympPlannerIsResumed) {
                Log.d(OLYMP_PLANNER_MAIN_TAG, "TIMEOUT: No conversion data received in 30s")
                olympPlannerResume(OlympPlannerAppsFlyerState.OlympPlannerError)
            }
        }
    }

    private fun olympPlannerResume(state: OlympPlannerAppsFlyerState) {
        olympPlannerConversionTimeoutJob?.cancel()
        if (state is OlympPlannerAppsFlyerState.OlympPlannerSuccess) {
            val convData = state.olympPlannerData ?: mutableMapOf()
            val deepData = olympPlannerDeepLinkData ?: mutableMapOf()
            val merged = mutableMapOf<String, Any>().apply {
                putAll(convData)
                for ((key, value) in deepData) {
                    if (!containsKey(key)) {
                        put(key, value)
                    }
                }
            }
            if (!olympPlannerIsResumed) {
                olympPlannerIsResumed = true
                olympPlannerConversionFlow.value = OlympPlannerAppsFlyerState.OlympPlannerSuccess(merged)
            }
        } else {
            if (!olympPlannerIsResumed) {
                olympPlannerIsResumed = true
                olympPlannerConversionFlow.value = state
            }
        }
    }

    private fun olympPlannerGetAppsflyerId(): String {
        val appsflyrid = AppsFlyerLib.getInstance().getAppsFlyerUID(this) ?: ""
        Log.d(OLYMP_PLANNER_MAIN_TAG, "AppsFlyer: AppsFlyer Id = $appsflyrid")
        return appsflyrid
    }

    private fun olympPlannerSetDebufLogger(appsflyer: AppsFlyerLib) {
        appsflyer.setDebugLog(true)
    }

    private fun olympPlannerMinTimeBetween(appsflyer: AppsFlyerLib) {
        appsflyer.setMinTimeBetweenSessions(0)
    }

    private fun olympPlannerGetApi(url: String, client: OkHttpClient?): OlympPlannerAppsApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(client ?: OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.notification_channel_name)
            val descriptionText = getString(R.string.notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableVibration(true)
                enableLights(true)
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        var olympPlannerInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
        val olympPlannerConversionFlow: MutableStateFlow<OlympPlannerAppsFlyerState> = MutableStateFlow(
            OlympPlannerAppsFlyerState.OlympPlannerDefault
        )
        var OLYMP_PLANNER_FB_LI: String? = null
        const val OLYMP_PLANNER_MAIN_TAG = "OlympPlannerMainTag"

        const val CHANNEL_ID = "olymp_planner_reminders"
    }
}