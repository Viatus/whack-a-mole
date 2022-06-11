package com.example.whack_a_mole.storage

interface Storage {
    fun setInt(key: String, value: Int)
    fun getInt(key: String): Int
}
