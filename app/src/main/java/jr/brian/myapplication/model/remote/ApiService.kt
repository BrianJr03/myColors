package jr.brian.myapplication.model.remote

import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("{color}")
    suspend fun getColors(
        @Path("color") color: String,
        @Query("number") number: Int
    ): MyColorResponse

    @GET("{color}")
    fun getColorsRx(
        @Path("color") color: String,
        @Query("number") number: Int
    ): Single<MyColorResponse>
}