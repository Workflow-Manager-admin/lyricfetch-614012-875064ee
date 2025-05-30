package com.example.lyricfetch.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lyricfetch.repository.LyricsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class LyricsState {
    object Initial : LyricsState()
    object Loading : LyricsState()
    data class Success(val lyrics: String) : LyricsState()
    data class Error(val message: String) : LyricsState()
}

class LyricsViewModel(private val repository: LyricsRepository) : ViewModel() {
    private val _state = MutableStateFlow<LyricsState>(LyricsState.Initial)
    val state: StateFlow<LyricsState> = _state

    fun searchLyrics(songName: String) {
        viewModelScope.launch {
            _state.value = LyricsState.Loading
            repository.getLyrics(songName)
                .onSuccess { lyrics ->
                    _state.value = LyricsState.Success(lyrics)
                }
                .onFailure { error ->
                    _state.value = LyricsState.Error(error.message ?: "Unknown error")
                }
        }
    }
}
