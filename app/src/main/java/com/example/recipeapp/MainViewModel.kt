package com.example.recipeapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.recipeapp.core.util.FavoriteDataStoreManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val favoriteManager = FavoriteDataStoreManager(application)

    val favoriteCount: StateFlow<Int> = favoriteManager
        .getFavoriteCountFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
}

object MainViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val application =
            checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) ->
                MainViewModel(application) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
