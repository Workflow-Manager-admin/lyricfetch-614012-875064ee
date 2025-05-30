package com.example.lyricfetch

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.lyricfetch.api.LyricsApi
import com.example.lyricfetch.repository.LyricsRepository
import com.example.lyricfetch.viewmodel.LyricsState
import com.example.lyricfetch.viewmodel.LyricsViewModel
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: LyricsViewModel
    private lateinit var searchInput: TextInputEditText
    private lateinit var progressBar: View
    private lateinit var lyricsText: android.widget.TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        searchInput = findViewById(R.id.searchInput)
        progressBar = findViewById(R.id.progressBar)
        lyricsText = findViewById(R.id.lyricsText)

        // Initialize API and ViewModel
        val api = Retrofit.Builder()
            .baseUrl("https://api.lyrics.ovh/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LyricsApi::class.java)

        val repository = LyricsRepository(api)
        viewModel = ViewModelProvider(this, LyricsViewModelFactory(repository))
            .get(LyricsViewModel::class.java)

        // Set up search button click listener
        findViewById<View>(R.id.searchButton).setOnClickListener {
            val songName = searchInput.text?.toString()
            if (!songName.isNullOrBlank()) {
                viewModel.searchLyrics(songName)
            }
        }

        // Observe state changes
        viewModel.state
            .onEach { state ->
                when (state) {
                    is LyricsState.Initial -> {
                        progressBar.visibility = View.GONE
                        lyricsText.text = ""
                    }
                    is LyricsState.Loading -> {
                        progressBar.visibility = View.VISIBLE
                        lyricsText.text = getString(R.string.loading)
                    }
                    is LyricsState.Success -> {
                        progressBar.visibility = View.GONE
                        lyricsText.text = state.lyrics
                    }
                    is LyricsState.Error -> {
                        progressBar.visibility = View.GONE
                        lyricsText.text = ""
                        Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
            .launchIn(lifecycleScope)
    }
}

class LyricsViewModelFactory(private val repository: LyricsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LyricsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LyricsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
