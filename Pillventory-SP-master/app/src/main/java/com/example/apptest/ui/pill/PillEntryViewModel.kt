package com.example.apptest.ui.pill

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class PillEntryViewModel : ViewModel() {
    private val _pillName = MutableStateFlow("")
    val pillName = _pillName

    private val _pillCount = MutableStateFlow(0)
    val pillCount = _pillCount

    private val _pillDescription = MutableStateFlow("")
    val pillDescription = _pillDescription

    fun onPillNameChange(newPillName: String) {
        _pillName.value = newPillName
    }

    fun onPillCountChange(newPillCount: Int) {
        _pillCount.value = newPillCount
    }

    fun onPillDescriptionChange(newPillDescription: String) {
        _pillDescription.value = newPillDescription
    }
}