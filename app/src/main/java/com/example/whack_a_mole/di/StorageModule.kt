package com.example.whack_a_mole.di

import com.example.whack_a_mole.storage.SharedPreferencesStorage
import com.example.whack_a_mole.storage.Storage
import dagger.Binds
import dagger.Module

@Module
abstract class StorageModule {
    @Binds
    abstract fun provideStorage(storage: SharedPreferencesStorage): Storage
}