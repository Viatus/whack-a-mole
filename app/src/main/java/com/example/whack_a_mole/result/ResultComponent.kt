package com.example.whack_a_mole.result

import com.example.whack_a_mole.di.ActivityScope
import dagger.Subcomponent

@ActivityScope
@Subcomponent
interface ResultComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): ResultComponent
    }

    fun inject(activity: ResultActivity)
}