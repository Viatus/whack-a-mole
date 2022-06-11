package com.example.whack_a_mole.game

import javax.inject.Inject
import kotlin.random.Random


class Game @Inject constructor() {
    fun getNewMoleNumber(): Int {
        holeWithMoleIndex = Random.nextInt(until = MAX_MOLES)
        return holeWithMoleIndex
    }

    var holeWithMoleIndex = 0
        private set

    fun hitMole(holeIndex: Int): Boolean {
        if (holeIndex == holeWithMoleIndex ) {
            holeWithMoleIndex = NO_MOLES_ANYWHERE
            return true
        }
        return false
    }

    fun hitMole(): Boolean {
        if (holeWithMoleIndex != NO_MOLES_ANYWHERE) {
            holeWithMoleIndex = NO_MOLES_ANYWHERE
            return true
        }
        return false
    }

    companion object {
        const val MAX_MOLES = 9
        const val NO_MOLES_ANYWHERE = -1
        const val MOLE_APPEARANCE_TIME_MILLISECONDS = 600L
    }
}