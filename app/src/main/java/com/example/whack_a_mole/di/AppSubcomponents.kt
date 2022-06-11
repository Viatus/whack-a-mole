package com.example.whack_a_mole.di

import com.example.whack_a_mole.game.GameComponent
import com.example.whack_a_mole.menu.MenuComponent
import com.example.whack_a_mole.result.ResultComponent
import dagger.Module

@Module(subcomponents = [MenuComponent::class, ResultComponent::class, GameComponent::class])
class AppSubcomponents {
}