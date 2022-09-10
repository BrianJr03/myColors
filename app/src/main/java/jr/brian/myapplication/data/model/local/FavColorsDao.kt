package jr.brian.myapplication.data.model.local

import androidx.room.*
import jr.brian.myapplication.data.model.remote.MyColor

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
}