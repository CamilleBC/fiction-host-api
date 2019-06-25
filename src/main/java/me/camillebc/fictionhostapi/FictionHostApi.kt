package me.camillebc.fictionhostapi

import kotlinx.coroutines.channels.ReceiveChannel

/**
 * All APIs added to the library must implement those functions.
 */
internal interface FictionHostApi {
    /**
     * getAllChapters takes a fiction as a parameter. It will use the chapters' URLs to download all of them
     *
     * @param fiction Chapters will be downloaded for this fiction
     */
    suspend fun getAllChapters(fiction: Fiction)

    suspend fun getChapter(chapter: Chapter)

    /**
     * getChapterRang takes a fiction and two indices as parameters. It will use the chapters' URLs to download the specified range.
     *
     * @param fiction Chapters will be downloaded for this fiction
     */
    suspend fun getChapterRange(fiction: Fiction, start: Int, end: Int)

    suspend fun getFiction(fictionId: String): Fiction
    suspend fun getTags(): List<String>
    /**
     * This function searches the host website with the specified parameters. It returns a receiver that can then be consumed.
     *
     * @param query Global query string
     * @param name Name (title) of the fiction to be found
     * @param author Search fiction written by specific author
     * @param tags List of fiction tags to search
     *
     * @return ReceiverChannel<Fiction> The receiver can be used in a Kotlin coroutine to consume available fiction. This allows to return search result in a progressive manner, instead of displaying nothing while the query is processed.
     */
    fun search(
        query: String? = null,
        name: String? = null,
        author: String? = null,
        tags: List<String>? = null
    ): ReceiveChannel<Fiction>
}
