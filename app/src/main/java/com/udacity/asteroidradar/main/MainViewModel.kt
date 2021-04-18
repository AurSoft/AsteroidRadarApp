package com.udacity.asteroidradar.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.NasaApi
import com.udacity.asteroidradar.api.NasaApiStatus
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AsteroidsFilter
import com.udacity.asteroidradar.repository.AsteroidsRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.UnknownHostException

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = getDatabase(application)
    private val asteroidsRepository = AsteroidsRepository(database)
    private val  _picOfTheDay=  MutableLiveData<PictureOfDay>()
    val picOfTheDay: LiveData<PictureOfDay>
        get() = _picOfTheDay
    private val _asteroidListStatus = MutableLiveData<NasaApiStatus>()
    val asteroidListStatus: LiveData<NasaApiStatus>
        get() = _asteroidListStatus
    private val _picOfDayStatus = MutableLiveData<NasaApiStatus>()
    val picOfDayStatus: LiveData<NasaApiStatus>
        get() = _picOfDayStatus

    init {
        getPicOfTheDay()
        getLatestAsteroids()
    }

    fun getPicOfTheDay() {
        _picOfDayStatus.value = NasaApiStatus.LOADING
        viewModelScope.launch {
            try {
                _picOfTheDay.value = NasaApi.retrofitService.getPicOfTheDay(Constants.API_KEY)
                _picOfDayStatus.value = NasaApiStatus.DONE
            } catch (e: Exception) {
                _picOfDayStatus.value = NasaApiStatus.ERROR
            }
        }
    }

    fun getLatestAsteroids() {
        _asteroidListStatus.value = NasaApiStatus.LOADING
        viewModelScope.launch {
            try {
                asteroidsRepository.refreshAsteroidsCache()
                _asteroidListStatus.value = NasaApiStatus.DONE
            } catch (e: HttpException) {
                _asteroidListStatus.value = NasaApiStatus.ERROR
                Log.i("getLatestAsteroids", e.toString())
            } catch (e: UnknownHostException) {
                _asteroidListStatus.value = NasaApiStatus.ERROR
                Log.i("getLatestAsteroids", e.toString())
            } catch (e: Exception) {
                Log.i("getLatestAsteroids", e.toString())
                _asteroidListStatus.value = NasaApiStatus.DONE
            }
        }
    }

    val asteroids = asteroidsRepository.asteroids

    private val _navigateToSelectedAsteroid = MutableLiveData<Asteroid>()
    val navigateToSelectedAsteroid: LiveData<Asteroid>
        get() = _navigateToSelectedAsteroid

    fun displayAsteroidDetails(asteroid: Asteroid) {
        _navigateToSelectedAsteroid.value = asteroid
    }

    fun displayPropertyDetailsComplete() {
        _navigateToSelectedAsteroid.value = null
    }

    fun setAsteroidListStatusToDone() {
        _asteroidListStatus.value = NasaApiStatus.DONE
    }

    fun filterWeeklyAsteroids() {
        asteroidsRepository.filterAsteroidsList(AsteroidsFilter.WEEKLY)
    }

    fun filterTodayAsteroids() {
        asteroidsRepository.filterAsteroidsList(AsteroidsFilter.TODAY)
    }
}