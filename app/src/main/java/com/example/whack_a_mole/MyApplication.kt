package com.example.whack_a_mole

import android.app.Application
import android.content.Context
import com.example.whack_a_mole.di.AppComponent
import com.example.whack_a_mole.di.DaggerAppComponent

open class MyApplication : Application() {
    val appComponent: AppComponent by lazy {
        initializeComponent()
    }

    open fun initializeComponent(): AppComponent {
        return DaggerAppComponent.factory().create(applicationContext)
    }
}

val Context.appComponent: AppComponent
    get() = when (this) {
        is MyApplication -> appComponent
        else -> this.applicationContext.appComponent
    }