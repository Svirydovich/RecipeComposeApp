package com.example.recipeapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.core.util.FavoriteDataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(favoriteManager: FavoriteDataStoreManager) :
    ViewModel() {

    val favoriteCount: StateFlow<Int> = favoriteManager
        .getFavoriteCountFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
}
