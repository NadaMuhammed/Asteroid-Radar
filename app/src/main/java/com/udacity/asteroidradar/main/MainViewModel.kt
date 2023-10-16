package com.udacity.asteroidradar.main

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants.API_KEY
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.data.AsteroidDatabase
import com.udacity.asteroidradar.repository.Repository
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.lang.Exception

class MainViewModel(application: Application) : AndroidViewModel(application) {
    val database = AsteroidDatabase.getDatabase(application)
    val asteroidRepository = Repository(database)

    var asteroidList2: MediatorLiveData<List<Asteroid>> = MediatorLiveData()

    @RequiresApi(Build.VERSION_CODES.O)
    fun todayAsteroids() {
        removeSource()
        asteroidList2.addSource(asteroidRepository.todayAsteroids()) {
            asteroidList2.value = it
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun weekAsteroids() {
        removeSource()
        asteroidList2.addSource(asteroidRepository.weekAsteroids()) {
            asteroidList2.value = it
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun allAsteroids() {
        removeSource()
        asteroidList2.addSource(asteroidRepository.asteroids()) {
            asteroidList2.value = it
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun removeSource() {
        asteroidList2.removeSource(asteroidRepository.asteroids())
        asteroidList2.removeSource(asteroidRepository.todayAsteroids())
        asteroidList2.removeSource(asteroidRepository.weekAsteroids())
    }

    init {
        viewModelScope.launch {
            try {
                asteroidList2.addSource(asteroidRepository.asteroids()) {
                    asteroidList2.value = it
                }
                asteroidRepository.refreshAsteroids()
                getPictureOfTheDay()
            }catch (e: Exception){
                Log.v("internet", e.message.toString())
            }
        }
    }

    private val _picture = MutableLiveData<PictureOfDay>()

    val picture: LiveData<PictureOfDay>
        get() = _picture

    private val _navigateToSelectedProperty = MutableLiveData<Asteroid>()

    val navigateToSelectedProperty: LiveData<Asteroid>
        get() = _navigateToSelectedProperty

    fun displayPropertyDetails(asteroid: Asteroid) {
        _navigateToSelectedProperty.value = asteroid
    }

    fun displayPropertyDetailsComplete() {
        _navigateToSelectedProperty.value = null
    }


    fun getPictureOfTheDay() {
            viewModelScope.launch {
                try {
                    val stringResponse = AsteroidApi.retrofitService.getPictureOfTheDay(API_KEY)
                    val jsonObject = JSONObject(stringResponse)
                    val pictureOfDay = PictureOfDay(
                        jsonObject.getString("media_type"),
                        jsonObject.getString("title"),
                        jsonObject.getString("url")
                    )
                    _picture.value = pictureOfDay

                } catch (e: Exception) {
                    Log.v("internet", e.message.toString())
                }
            }
    }


}