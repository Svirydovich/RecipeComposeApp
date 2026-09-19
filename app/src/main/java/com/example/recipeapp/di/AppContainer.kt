package com.example.recipeapp.di

import android.content.Context
import com.example.recipeapp.BuildConfig
import com.example.recipeapp.core.network.NetworkConfig
import com.example.recipeapp.core.network.api.RecipesApiService
import com.example.recipeapp.data.database.RecipesDatabase
import com.example.recipeapp.data.repository.RecipesRepository
import com.example.recipeapp.data.repository.RecipesRepositoryImpl
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

class AppContainer(context: Context) {
    private val json = Json { ignoreUnknownKeys = true; coerceInputValues = true }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(loggingInterceptor)
        .build()
    private val retrofit = Retrofit.Builder()
        .baseUrl(NetworkConfig.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    private val recipesApi: RecipesApiService =
        retrofit.create(RecipesApiService::class.java)
    private val recipesDatabase: RecipesDatabase = RecipesDatabase.buildDatabase(context)

    val recipesRepository: RecipesRepository =
        RecipesRepositoryImpl(recipesApi, recipesDatabase)
}
