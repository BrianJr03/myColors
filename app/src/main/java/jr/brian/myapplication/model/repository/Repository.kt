package jr.brian.myapplication.model.repository

import jr.brian.myapplication.model.remote.MyColorResponse

interface Repository {
    suspend fun getColors(numOfColors: Int) : MyColorResponse
}