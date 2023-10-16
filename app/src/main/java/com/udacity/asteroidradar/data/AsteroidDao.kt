package com.udacity.asteroidradar.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.udacity.asteroidradar.Asteroid

@Dao
interface AsteroidDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAsteroids(vararg asteroids: DatabaseAsteroid)

    @Query("SELECT * FROM asteroids_table ORDER BY closeApproachDate DESC")
    fun getAllAsteroids(): LiveData<List<DatabaseAsteroid>>

    @Query("SELECT * FROM asteroids_table WHERE closeApproachDate = :today ORDER BY closeApproachDate DESC")
    fun getTodayAsteroids(today: String): LiveData<List<DatabaseAsteroid>>

    @Query("SELECT * FROM asteroids_table WHERE closeApproachDate BETWEEN :start AND :end ORDER BY closeApproachDate DESC")
    fun getWeekAsteroids(start: String, end: String): LiveData<List<DatabaseAsteroid>>

    @Query("DELETE FROM asteroids_table")
    suspend fun clear()

}