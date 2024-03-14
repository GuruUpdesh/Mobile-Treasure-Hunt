/* Assignment 6: Mobile Treasure Hunt
    Guru Updesh Singh / singguru@oregonstate.edu
    CS 492 / Oregon State University
*/

package com.example.assignment6.ui

import androidx.lifecycle.ViewModel
import com.example.assignment6.data.ClueDataSource
import com.example.assignment6.model.Clue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import androidx.compose.runtime.getValue

class ClueViewModel : ViewModel() {
    private val _clues = ClueDataSource.clues
    private val _currentClueIndex = MutableStateFlow(0)
    val currentClueIndex: StateFlow<Int> = _currentClueIndex

    private val _hintVisible = MutableStateFlow(false)
    val hintVisible: StateFlow<Boolean> = _hintVisible

    fun showHint() {
        _hintVisible.value = true
    }

    private val _showIncorrectAlert = MutableStateFlow(false)
    val showIncorrectAlert: StateFlow<Boolean> = _showIncorrectAlert

    fun showAlert() {
        _showIncorrectAlert.value = true
    }

    fun closeAlert() {
        _showIncorrectAlert.value = false
    }


    fun nextClue() {
        if (_currentClueIndex.value < _clues.size - 1) {
            _currentClueIndex.value += 1
            _hintVisible.value = false
        }
    }

    fun resetHunt() {
        _currentClueIndex.value = 0
        _hintVisible.value = false
        _showIncorrectAlert.value = false
    }
}
