package jr.brian.myapplication.data.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "start_up_table")
data class StartUpIntro(
    @PrimaryKey val hasBeenPassed: Boolean,
)