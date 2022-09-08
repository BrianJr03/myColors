package jr.brian.myapplication.data.model.repository

import jr.brian.myapplication.data.model.remote.MyColorResponse

interface Repository {
    suspend fun getColors(color: String, numOfColors: Int): MyColorResponse
}