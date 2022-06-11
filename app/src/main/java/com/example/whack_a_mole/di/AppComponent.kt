package com.example.whack_a_mole.di

import android.content.Context
import com.example.whack_a_mole.game.GameComponent
import com.example.whack_a_mole.menu.MenuComponent
import com.example.whack_a_mole.result.ResultComponent
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [StorageModule::class, AppSubcomponents::class])
interface AppComponent {
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }

    fun menuComponent(): MenuComponent.Factory

    fun resultComponent(): ResultComponent.Factory

    fun gameComponent(): GameComponent.Factory
}