package jr.brian.myapplicatid.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth
import jr.brian.myapplication.data.model.local.AppDatabase
import jr.brian.myapplication.data.model.local.FavColorsDao
import jr.brian.myapplication.data.model.remote.MyColor
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class FavColorsDaoTest {

    private lateinit var appDB: AppDatabase
    private lateinit var dao: FavColorsDao

    private val color1 = MyColor(hex = "#E48762")
    private val color2 = MyColor(hex = "#CEF8B0")
    private val color3 = MyColor(hex = "#77ECFE")

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun init() {
        appDB = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()
        dao = appDB.dao()
    }

    @After
    fun close() {
        appDB.close()
    }

    @Test
    fun testInsertFavColors() {
        runTest(StandardTestDispatcher()) {
            dao.apply {
                insertFavColor(color1)
                insertFavColor(color2)
                insertFavColor(color3)
            }

            val favColors = dao.getFavColors()

            Truth.assertThat(favColors).containsExactly(color1, color2, color3)
        }
    }

    @Test
    fun testRemoveFavColor() {
        runTest(StandardTestDispatcher()) {
            dao.apply {
                insertFavColor(color1)
                insertFavColor(color2)
                insertFavColor(color3)
                removeFavColor(color1)
            }

            val favColors = dao.getFavColors()

            Truth.assertThat(favColors).containsExactly(color2, color3)
        }
    }

    @Test
    fun testRemoveAllFavColors() {
        runTest(StandardTestDispatcher()) {
            dao.apply {
                insertFavColor(color1)
                insertFavColor(color2)
                insertFavColor(color3)
                removeAllFavColors()
            }

            val favColors = dao.getFavColors()

            Truth.assertThat(favColors).isEmpty()
        }
    }
}