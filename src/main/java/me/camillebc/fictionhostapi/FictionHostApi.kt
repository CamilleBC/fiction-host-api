package me.camillebc.fictionhostapi

import java.io.File

internal interface FictionHostApi {
    suspend fun getAllChapters(fiction: Fiction)
    suspend fun getChapter(chapter: Chapter)
    suspend fun getChapterRange(fiction: Fiction, start: Int, end: Int)
    suspend fun getFiction(fictionId: String): Fiction
    suspend fun getTags(): List<String>
    suspend fun search(
        query: String? = null,
        name: String? = null,
        author: String? = null,
        tags: List<String>? = null
    ): List<Fiction>
}
