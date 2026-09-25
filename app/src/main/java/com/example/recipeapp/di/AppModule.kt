package com.example.recipeapp.di

import android.content.Context
import com.example.recipeapp.core.network.NetworkConfig
import com.example.recipeapp.core.network.api.RecipesApiService
import com.example.recipeapp.data.database.RecipesDatabase
import com.example.recipeapp.data.repository.RecipesRepository
import com.example.recipeapp.data.repository.RecipesRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRecipesApiService(): RecipesApiService {
        return Retrofit.Builder()
            .baseUrl(NetworkConfig.BASE_URL)
            .build()
            .create(RecipesApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideRecipesDatabase(
        @ApplicationContext context: Context
    ): RecipesDatabase {
        return RecipesDatabase.buildDatabase(context)
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindRecipesRepository(
        impl: RecipesRepositoryImpl
    ): RecipesRepository
}
