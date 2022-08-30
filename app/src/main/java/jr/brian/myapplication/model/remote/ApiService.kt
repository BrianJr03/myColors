package jr.brian.myapplication.model.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("{color}")
    suspend fun getColors(
        @Path("color") color: String,
        @Query("number") number: Int
    ): MyColorResponse
}