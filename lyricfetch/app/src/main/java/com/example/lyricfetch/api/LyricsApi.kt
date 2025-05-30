package com.example.lyricfetch.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface LyricsApi {
    @GET("lyrics")
    suspend fun getLyrics(@Query("song") songName: String): Response<LyricsResponse>
}

data class LyricsResponse(
    val lyrics: String?,
    val error: String?
)
