package com.udacity.asteroidradar

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.udacity.asteroidradar.database.AsteroidDao
import com.udacity.asteroidradar.database.AsteroidsDatabase
import com.udacity.asteroidradar.database.DatabaseAsteroid
import com.udacity.asteroidradar.repository.AsteroidsRepository
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.HttpException
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class RefreshDataWorkerLogicTest {

    private lateinit var asteroidDao: AsteroidDao
    private lateinit var db: AsteroidsDatabase
    private lateinit var repository: AsteroidsRepository
    @Before
    fun createDbAndRepository() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, AsteroidsDatabase::class.java)
                .allowMainThreadQueries()
                .build()
        asteroidDao = db.asteroidDao
        repository = AsteroidsRepository(db)
        runBlocking {
            try {
                repository.refreshAsteroidsCache()
            } catch (e: HttpException) {
            }
        }
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun tryWorkerLogic() {
        val list = listOf<Asteroid>(
                Asteroid(99999, "test", "2021-05-05",
                        1.0, 1.0, 1.0, 1.0, true)
        )
        asteroidDao.insertAll(*list.asDatabaseModel())
        var asteroid: DatabaseAsteroid?  = asteroidDao.getAsteroidById(99999);
        Assert.assertFalse(asteroid?.id != 99999L)
        asteroidDao.deleteOlderAsteroids()
        asteroid = asteroidDao.getAsteroidById(99999);
        Assert.assertTrue(asteroid == null)
    }
}