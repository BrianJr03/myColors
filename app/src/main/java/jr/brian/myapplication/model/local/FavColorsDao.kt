package jr.brian.myapplication.model.local

import androidx.room.*
import jr.brian.myapplication.model.remote.MyColor

@Dao
interface FavColorsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFavColor(color: MyColor)

    @Query("SELECT * FROM color_table")
    fun getFavColors(): List<MyColor>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun passStartUp(sui: StartUpIntro)

    @Query("SELECT * FROM start_up_table")
    fun getStartUpPass(): List<StartUpIntro>

    @Delete
    fun removeFavColor(color: MyColor)

    @Query("DELETE FROM color_table")
    fun removeAllFavColors()
}