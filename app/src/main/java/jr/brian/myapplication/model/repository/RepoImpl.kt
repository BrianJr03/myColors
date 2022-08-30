package jr.brian.myapplication.model.repository

import jr.brian.myapplication.model.remote.ApiService
import jr.brian.myapplication.model.remote.MyColorResponse

class RepoImpl(private val apiService: ApiService) : Repository {
    override suspend fun getColors(color: String, numOfColors: Int): MyColorResponse {
        return apiService.getColors(color, numOfColors)
    }
}
