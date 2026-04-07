package com.example.smartassist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartassist.repository.ClaudeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class UiState(
    val result: String = "",
    val isLoading: Boolean = false,
    val error: String = ""
)

class MainViewModel : ViewModel() {

    private val repository = ClaudeRepository()

    private val _summarizeState = MutableStateFlow(UiState())
    val summarizeState: StateFlow<UiState> = _summarizeState

    private val _translateState = MutableStateFlow(UiState())
    val translateState: StateFlow<UiState> = _translateState

    private val _recipeState = MutableStateFlow(UiState())
    val recipeState: StateFlow<UiState> = _recipeState

    fun summarize(text: String) {
        viewModelScope.launch {
            _summarizeState.value = UiState(isLoading = true)
            try {
                val result = repository.summarize(text)
                _summarizeState.value = UiState(result = result)
            } catch (e: Exception) {
                _summarizeState.value = UiState(error = "오류: ${e.message}")
            }
        }
    }

    fun translate(text: String, targetLang: String) {
        viewModelScope.launch {
            _translateState.value = UiState(isLoading = true)
            try {
                val result = repository.translate(text, targetLang)
                _translateState.value = UiState(result = result)
            } catch (e: Exception) {
                _translateState.value = UiState(error = "오류: ${e.message}")
            }
        }
    }

    fun getRecipe(ingredients: String) {
        viewModelScope.launch {
            _recipeState.value = UiState(isLoading = true)
            try {
                val result = repository.getRecipe(ingredients)
                _recipeState.value = UiState(result = result)
            } catch (e: Exception) {
                _recipeState.value = UiState(error = "오류: ${e.message}")
            }
        }
    }
}
