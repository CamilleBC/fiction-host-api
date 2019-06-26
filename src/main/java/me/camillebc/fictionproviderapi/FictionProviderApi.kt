package me.camillebc.fictionproviderapi

import kotlinx.coroutines.channels.ReceiveChannel

/**
 * All APIs added to the library must implement those functions.
 */
interface FictionProviderApi {
    val baseUrl: String
    /**
     * getFictionChapters takes a fictionMetadata and two optional indices as parameters.
     * It will use the chapters' IDs to download the specified range (defaults to all)
     *
     * @param fictionMetadata Holds the chapter IDs for this fiction
     * @param start Start index for the chapter download (defaults to 0)
     * @param end End index for the chapter download (default to last chapter ID)
     */
    suspend fun getFictionChapters(
        fictionMetadata: FictionMetadata,
        start: Int = 0,
        end: Int = fictionMetadata.chapters.lastIndex
    ): List<Chapter>

    /**
     * Returns a chapter content and title given it's ID.
     *
     * @param chapterId Chapter to download
     */
    suspend fun getChapter(chapterId: String): Chapter

    /**
     * Returns a list of all chapter's content.
     * Download is run asynchronously but respects the orders of the chapterIds list.
     *
     * @param chapterIds List of chapters to download
     */
    suspend fun getChapters(chapterIds: List<String>): List<Chapter>

    suspend fun getFiction(fictionId: String): FictionMetadata
    suspend fun getTags(): List<String>
    /**
     * This function searches the host website with the specified parameters. It returns a receiver that can then be consumed.
     *
     * @param query Global query string
     * @param name Name (title) of the fiction to be found
     * @param author Search fiction written by specific author
     * @param tags List of fiction tags to search
     *
     * @return ReceiverChannel<FictionMetadata> The receiver can be used in a Kotlin coroutine to consume available fiction. This allows to return search result in a progressive manner, instead of displaying nothing while the query is processed.
     */
    fun search(
        query: String? = null,
        name: String? = null,
        author: String? = null,
        tags: List<String>? = null
    ): ReceiveChannel<FictionMetadata>
}
