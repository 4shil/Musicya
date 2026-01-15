package com.fourshil.musicya.ui.nowplaying

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fourshil.musicya.data.model.Song
import com.fourshil.musicya.player.PlayerController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NowPlayingViewModel @Inject constructor(
    private val playerController: PlayerController
) : ViewModel() {

    val currentSong = playerController.currentSong
    val isPlaying = playerController.isPlaying
    val shuffleEnabled = playerController.shuffleEnabled
    val repeatMode = playerController.repeatMode

    private val _position = MutableStateFlow(0L)
    val position = _position.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    val duration = _duration.asStateFlow()

    init {
        playerController.connect()
        startPositionUpdater()
    }

    private fun startPositionUpdater() {
        viewModelScope.launch {
            while (true) {
                playerController.controller?.let {
                    _position.value = it.currentPosition
                    _duration.value = it.duration.coerceAtLeast(0)
                }
                delay(500)
            }
        }
    }

    fun togglePlayPause() = playerController.togglePlayPause()
    fun skipToNext() = playerController.skipToNext()
    fun skipToPrevious() = playerController.skipToPrevious()
    fun toggleShuffle() = playerController.toggleShuffle()
    fun toggleRepeat() = playerController.toggleRepeat()
    fun seekTo(position: Long) = playerController.seekTo(position)
}
