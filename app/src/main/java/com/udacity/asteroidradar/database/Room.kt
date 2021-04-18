package com.udacity.asteroidradar.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import com.udacity.asteroidradar.utils.getTodayDateAsString

@Dao
interface AsteroidDao {
    @Query("select * from asteroid a where a.closeApproachDate >= :fromThisDate order by a.closeApproachDate asc")
    fun getAsteroidsFromDate(fromThisDate: String = getTodayDateAsString()) : LiveData<List<DatabaseAsteroid>>

    @Query("select * from asteroid a where a.closeApproachDate = :toDate order by a.closeApproachDate asc")
    fun getAsteroidsToDate(toDate: String = getTodayDateAsString()) : LiveData<List<DatabaseAsteroid>>

    @Query("select * from asteroid a where a.id = :id")
    fun getAsteroidById(id: Long) : DatabaseAsteroid?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg asteroids: DatabaseAsteroid)

    @Query("delete from asteroid where asteroid.id in (select id from asteroid a where a.closeApproachDate < :beforeThisDate)")
    fun deleteOlderAsteroids(beforeThisDate: String  = getTodayDateAsString())
}

@Database(entities = [DatabaseAsteroid::class], version = 1)
abstract class AsteroidsDatabase : RoomDatabase() {
    abstract val asteroidDao: AsteroidDao
}

private lateinit var INSTANCE: AsteroidsDatabase

fun getDatabase(context: Context): AsteroidsDatabase {
    synchronized(AsteroidsDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    AsteroidsDatabase::class.java,
                    "asteroids")
                .build()
        }
    }

    return INSTANCE
}