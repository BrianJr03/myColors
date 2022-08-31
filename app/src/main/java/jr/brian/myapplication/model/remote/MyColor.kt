package jr.brian.myapplication.model.remote

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "color_table")
data class MyColor(
    @PrimaryKey val hex: String,
    val hsl: String,
    val rgb: String
)