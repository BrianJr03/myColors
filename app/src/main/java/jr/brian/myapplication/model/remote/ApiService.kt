package jr.brian.myapplication.model.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("red")
    suspend fun getRandomColor(@Query("number") number: Int): MyColorResponse
}