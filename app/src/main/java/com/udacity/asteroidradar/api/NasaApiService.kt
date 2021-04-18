package com.udacity.asteroidradar.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.PictureOfDay
import kotlinx.coroutines.Deferred
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).add(AsteroidListAdapter).build()

interface NasaApiService {
    @GET("neo/rest/v1/feed")
    suspend fun getAsteroids(
        @Query("api_key") apiKey: String
    ) : List<Asteroid>

    @GET("planetary/apod")
    suspend fun getPicOfTheDay(
        @Query("api_key") apiKey: String
    ) : PictureOfDay
}

object NasaApi {
    private val retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl(Constants.BASE_URL)
        .build()

    val retrofitService : NasaApiService by lazy { retrofit.create(NasaApiService::class.java) }
}

enum class NasaApiStatus { LOADING, ERROR, DONE }