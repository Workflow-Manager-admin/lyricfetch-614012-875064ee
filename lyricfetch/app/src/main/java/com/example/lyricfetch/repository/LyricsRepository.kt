package com.example.lyricfetch.repository

import com.example.lyricfetch.api.LyricsApi
import com.example.lyricfetch.api.LyricsResponse
import retrofit2.Response

class LyricsRepository(private val api: LyricsApi) {
    suspend fun getLyrics(songName: String): Result<String> {
        return try {
            val response = api.getLyrics(songName)
            if (response.isSuccessful && response.body()?.lyrics != null) {
                Result.success(response.body()!!.lyrics!!)
            } else {
                Result.failure(Exception(response.body()?.error ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
