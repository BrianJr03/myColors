package jr.brian.myapplication.model.repository

import io.reactivex.rxjava3.core.Single
import jr.brian.myapplication.model.remote.ApiService
import jr.brian.myapplication.model.remote.MyColorResponse

class RepoImpl(private val apiService: ApiService) : Repository {
    override suspend fun getColors(color: String, numOfColors: Int): MyColorResponse {
        return apiService.getColors(color, numOfColors)
    }

    override fun getColorsRx(color: String, numOfColors: Int): Single<MyColorResponse> {
        return apiService.getColorsRx(color, numOfColors)
    }
}