package jr.brian.myapplication.model.repository

import io.reactivex.rxjava3.core.Single
import jr.brian.myapplication.model.remote.MyColorResponse

interface Repository {
    suspend fun getColors(color: String, numOfColors: Int) : MyColorResponse

    fun getColorsRx(color: String, numOfColors: Int) : Single<MyColorResponse>
}