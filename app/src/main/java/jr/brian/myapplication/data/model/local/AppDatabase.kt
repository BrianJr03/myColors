package jr.brian.myapplication.data.model.local

import androidx.room.Database
import androidx.room.RoomDatabase
import jr.brian.myapplication.data.model.remote.MyColor

@Database(entities = [MyColor::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dao(): FavColorsDao
}