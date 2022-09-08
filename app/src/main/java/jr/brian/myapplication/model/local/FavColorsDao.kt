package jr.brian.myapplication.model.local

import androidx.room.*
import jr.brian.myapplication.model.remote.MyColor

@Dao
interface FavColorsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFavColor(color: MyColor)

    @Query("SELECT * FROM color_table")
    fun getFavColors(): List<MyColor>

    @Delete
    fun removeFavColor(color: MyColor)

    @Query("DELETE FROM color_table")
    fun removeAllFavColors()

    // TODO - Save in DataStore
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun passStartUp(sui: StartUpIntro)

    // TODO - Save in DataStore
    @Query("SELECT * FROM start_up_table")
    fun getStartUpPass(): List<StartUpIntro>
}