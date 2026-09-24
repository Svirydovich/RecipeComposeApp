package com.example.recipeapp.di

interface Factory<T> {
    fun create(): T
}
