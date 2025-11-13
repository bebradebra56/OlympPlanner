package com.olympplanner.app.bkgoprte.data.repo

import android.util.Log
import com.olympplanner.app.bkgoprte.domain.model.OlympPlannerEntity
import com.olympplanner.app.bkgoprte.domain.model.OlympPlannerParam
import com.olympplanner.app.bkgoprte.presentation.app.OlympPlannerApplication.Companion.OLYMP_PLANNER_MAIN_TAG
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface OlympPlannerApi {
    @Headers("Content-Type: application/json")
    @POST("config.php")
    fun olympPlannerGetClient(
        @Body jsonString: JsonObject,
    ): Call<OlympPlannerEntity>
}


private const val OLYMP_PLANNER_MAIN = "https://ollympplanner.com/"
class OlympPlannerRepository {

    suspend fun olympPlannerGetClient(
        olympPlannerParam: OlympPlannerParam,
        olympPlannerConversion: MutableMap<String, Any>?
    ): OlympPlannerEntity? {
        val gson = Gson()
        val api = olympPlannerGetApi(OLYMP_PLANNER_MAIN, null)

        val olympPlannerJsonObject = gson.toJsonTree(olympPlannerParam).asJsonObject
        olympPlannerConversion?.forEach { (key, value) ->
            val element: JsonElement = gson.toJsonTree(value)
            olympPlannerJsonObject.add(key, element)
        }
        return try {
            val olympPlannerRequest: Call<OlympPlannerEntity> = api.olympPlannerGetClient(
                jsonString = olympPlannerJsonObject,
            )
            val olympPlannerResult = olympPlannerRequest.awaitResponse()
            Log.d(OLYMP_PLANNER_MAIN_TAG, "Retrofit: Result code: ${olympPlannerResult.code()}")
            if (olympPlannerResult.code() == 200) {
                Log.d(OLYMP_PLANNER_MAIN_TAG, "Retrofit: Get request success")
                Log.d(OLYMP_PLANNER_MAIN_TAG, "Retrofit: Code = ${olympPlannerResult.code()}")
                Log.d(OLYMP_PLANNER_MAIN_TAG, "Retrofit: ${olympPlannerResult.body()}")
                olympPlannerResult.body()
            } else {
                null
            }
        } catch (e: java.lang.Exception) {
            Log.d(OLYMP_PLANNER_MAIN_TAG, "Retrofit: Get request failed")
            Log.d(OLYMP_PLANNER_MAIN_TAG, "Retrofit: ${e.message}")
            null
        }
    }


    private fun olympPlannerGetApi(url: String, client: OkHttpClient?) : OlympPlannerApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(client ?: OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create()
    }


}
