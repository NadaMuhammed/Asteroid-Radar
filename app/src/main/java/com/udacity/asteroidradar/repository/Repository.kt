package com.udacity.asteroidradar.repository


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants.API_KEY
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.data.AsteroidDatabase
import com.udacity.asteroidradar.data.asDatabaseModel
import com.udacity.asteroidradar.data.asDomainModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.time.LocalDate


class Repository(private val database: AsteroidDatabase) {

    fun asteroids(): LiveData<List<Asteroid>> {
        return Transformations.map(database.AsteroidDao().getAllAsteroids()) {
            it.asDomainModel()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun todayAsteroids(): LiveData<List<Asteroid>> {
        return Transformations.map(database.AsteroidDao().getTodayAsteroids(LocalDate.now().toString())) {
            it.asDomainModel()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun weekAsteroids(): LiveData<List<Asteroid>> {
        return Transformations.map(
            database.AsteroidDao().getWeekAsteroids(LocalDate.now().toString(), LocalDate.now().plusDays(7).toString())) {
            it.asDomainModel()
        }
    }

    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            val stringResponse = AsteroidApi.retrofitService.getAllAsteroids(API_KEY)
            val asteroidArrayList = parseAsteroidsJsonResult(JSONObject(stringResponse))
            database.AsteroidDao().addAsteroids(*asteroidArrayList.asDatabaseModel())
        }
    }


}