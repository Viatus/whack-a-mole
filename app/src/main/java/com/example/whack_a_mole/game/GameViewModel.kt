package com.example.whack_a_mole.game

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.whack_a_mole.di.ActivityScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@ActivityScope
class GameViewModel @Inject constructor(private val game: Game) : ViewModel() {

    private val molesBeaten: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>(
            0
        )
    }

    fun getBeatenMoles(): LiveData<Int> {
        return molesBeaten
    }

    var countDownInit = 30
        private set

    fun countDownTimerFlow() = flow<Int> {
        var time = countDownInit
        emit(time)
        while (true) {
            time--
            delay(1000L)
            countDownInit = time
            emit(time)
        }
    }

    fun moleFlow() = flow {
        delay(1000)
        emit(game.getNewMoleNumber())
        while (true) {
            Log.i("gameviewmodel", "new mole at " + game.holeWithMoleIndex)
            delay(Game.MOLE_APPEARANCE_TIME_MILLISECONDS)
            emit(game.getNewMoleNumber())
        }
    }

    fun setupCountDownTimer(initialCountDown: Int) {
        countDownInit = initialCountDown
    }

    fun hitMole(holeIndex: Int): Boolean {
        Log.i("gameviewmodel", "hit hole at $holeIndex")

        return if (game.hitMole(holeIndex)) {
            molesBeaten.value = (molesBeaten.value ?: 0) + 1
            true
        } else {
            false
        }
    }

    fun hitMole(): Boolean {
        return if (game.hitMole()) {
            molesBeaten.value = (molesBeaten.value ?: 0) + 1
            true
        } else {
            false
        }
    }
}