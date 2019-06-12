package me.camillebc.fictionhostapi

import java.io.File

data class Fiction(
    val name: String,
    val host: String,
    val hostUrl: Long,
    val author: String = "unknown",
    val authorUrl: String?,
    val description: String = "No description available.",
    val imageUrl: String?,
    val chapters: List<File>
) {}