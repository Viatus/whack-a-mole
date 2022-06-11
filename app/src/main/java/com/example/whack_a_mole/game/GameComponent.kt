package com.example.whack_a_mole.game

import com.example.whack_a_mole.di.ActivityScope
import dagger.Subcomponent

@ActivityScope
@Subcomponent
interface GameComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): GameComponent
    }

    fun inject(activity: GameActivity)
}