package jr.brian.myapplication.model.repository

import jr.brian.myapplication.model.remote.ApiService
import jr.brian.myapplication.model.remote.MyColorResponse

class RepositoryImplementation(private val apiService: ApiService) : Repository {
    override suspend fun getColors(numOfColors: Int): MyColorResponse {
        return apiService.getRandomColor(numOfColors)
    }
}
