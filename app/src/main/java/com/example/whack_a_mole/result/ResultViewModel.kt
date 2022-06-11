package com.example.whack_a_mole.result

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.whack_a_mole.di.ActivityScope
import com.example.whack_a_mole.record.RecordManager
import javax.inject.Inject

@ActivityScope
class ResultViewModel @Inject constructor(private val recordManager: RecordManager): ViewModel() {
    private val record: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>(
            loadRecord()
        )
    }

    private fun loadRecord() = recordManager.record

    fun getRecord(): LiveData<Int> {
        return record
    }

    fun updateRecord(newRecord: Int): Boolean {
        return recordManager.updateRecord(newRecord)
    }
}