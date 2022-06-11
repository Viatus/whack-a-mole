package com.example.whack_a_mole.menu

import com.example.whack_a_mole.di.ActivityScope
import dagger.Subcomponent

@ActivityScope
@Subcomponent
interface MenuComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): MenuComponent
    }

    fun inject(activity: MenuActivity)
}