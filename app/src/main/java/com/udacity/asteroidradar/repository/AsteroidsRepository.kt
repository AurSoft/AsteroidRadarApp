package com.udacity.asteroidradar.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.api.NasaApi
import com.udacity.asteroidradar.asDatabaseModel
import com.udacity.asteroidradar.database.AsteroidsDatabase
import com.udacity.asteroidradar.database.DatabaseAsteroid
import com.udacity.asteroidradar.database.asDomainModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AsteroidsRepository(private val database: AsteroidsDatabase) {
    private var filter = MutableLiveData<AsteroidsFilter>(AsteroidsFilter.WEEKLY)
    val asteroids: LiveData<List<Asteroid>> = Transformations.switchMap(filter) {
        when(it) {
            AsteroidsFilter.WEEKLY ->
                Transformations.map(database.asteroidDao.getAsteroidsFromDate()) { list ->
                    list.asDomainModel()
                }
            AsteroidsFilter.TODAY ->
                Transformations.map(database.asteroidDao.getAsteroidsToDate()) { list ->
                    list.asDomainModel()
                }
            else -> null
        }
    }

    suspend fun refreshAsteroidsCache() {
        withContext(Dispatchers.IO) {
            val asteroids = NasaApi.retrofitService.getAsteroids(Constants.API_KEY)
            database.asteroidDao.insertAll(*asteroids.asDatabaseModel())
        }
    }

    fun filterAsteroidsList(listFilter: AsteroidsFilter) {
        filter.value = listFilter
    }

}

enum class AsteroidsFilter { WEEKLY, TODAY }