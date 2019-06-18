package me.camillebc.fictionhostapi

import java.io.File

internal interface FictionHostApi {
    suspend fun getChapter(fictionId: String, chapterId: String): File
    suspend fun getChapterRange(fictionId: String, startChapterId: String, endChapterId: String): List<File>
    suspend fun getFiction(fictionId: String): Fiction
    suspend fun getTags(): List<String>
    suspend fun search(): List<Fiction>
}
