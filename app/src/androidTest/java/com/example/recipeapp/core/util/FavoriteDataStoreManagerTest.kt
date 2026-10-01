package com.example.recipeapp.core.util

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavoriteDataStoreTest {
    private lateinit var context: Context
    private lateinit var manager: FavoriteDataStoreManager

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        manager = FavoriteDataStoreManager(context)
    }

    @After
    fun tearDown() {
        runBlocking { context.dataStore.edit { it.clear() } }
    }

    @Test
    fun addFavoriteSavesRecipeId() = runTest {
        manager.addFavorite(recipeId = 42)
        assertTrue(manager.getFavoriteIdsFlow().first().contains("42"))
    }

    @Test
    fun removeFromFavoritesDeletesRecipeId() = runTest {
        manager.addFavorite(recipeId = 42)
        assertTrue(manager.getFavoriteIdsFlow().first().contains("42"))

        manager.removeFavorite(recipeId = 42)

        assertFalse(manager.getFavoriteIdsFlow().first().contains("42"))
    }

    @Test
    fun favoritesFlowEmitsUpdatesReactively() = runTest {
        manager.getFavoriteIdsFlow().test {
            val initial = awaitItem()
            assertTrue(initial.isEmpty())

            manager.addFavorite(recipeId = 101)
            val afterAdd = awaitItem()
            assertEquals(1, afterAdd.size)
            assertTrue(afterAdd.contains("101"))

            manager.addFavorite(recipeId = 202)
            val afterSecondAdd = awaitItem()
            assertEquals(2, afterSecondAdd.size)
            assertTrue(afterSecondAdd.contains("101"))
            assertTrue(afterSecondAdd.contains("202"))

            manager.removeFavorite(recipeId = 101)
            val afterRemove = awaitItem()
            assertEquals(1, afterRemove.size)
            assertFalse(afterRemove.contains("101"))
            assertTrue(afterRemove.contains("202"))

            cancelAndIgnoreRemainingEvents()
        }
    }
}
