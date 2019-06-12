package me.camillebc.fictionhostapi

import java.io.File

internal interface FictionHostApi {
    suspend fun getChapter(id: String): File
    suspend fun getChapterRange(start: String, end: String): List<File>
    suspend fun getFiction(id: String, download: Boolean): Fiction
    suspend fun getTags(): List<String>
    suspend fun search(): List<Fiction>
}
