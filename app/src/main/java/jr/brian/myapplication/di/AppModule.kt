package jr.brian.myapplication.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory
import jr.brian.myapplication.data.model.local.AppDatabase
import jr.brian.myapplication.data.model.local.FavColorsDao
import jr.brian.myapplication.data.model.remote.ApiService
import jr.brian.myapplication.data.model.remote.Constants.BASE_URL
import jr.brian.myapplication.data.model.repository.RepoImpl
import jr.brian.myapplication.data.model.repository.Repository
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAPI(): ApiService {
        val retrofit = Retrofit.Builder()
            .client(OkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .baseUrl(BASE_URL)
            .build()
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideRepository(apiService: ApiService): Repository = RepoImpl(apiService)

    @Provides
    @Singleton
    fun provideDao(appDatabase: AppDatabase): FavColorsDao = appDatabase.dao()

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "fav-colors"
        )
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
    }
}