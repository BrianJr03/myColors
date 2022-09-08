package jr.brian.myapplication.data.model.repository

import jr.brian.myapplication.data.model.remote.ApiService
import jr.brian.myapplication.data.model.remote.MyColorResponse

class RepoImpl(private val apiService: ApiService) : Repository {
    override suspend fun getColors(color: String, numOfColors: Int): MyColorResponse {
        return apiService.getColors(color, numOfColors)
    }
}