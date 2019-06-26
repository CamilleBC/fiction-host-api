package me.camillebc.fictionproviderapi.royalroad

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.coroutineScope
import me.camillebc.fictionproviderapi.Chapter
import me.camillebc.fictionproviderapi.FictionMetadata
import me.camillebc.fictionproviderapi.FictionProviderApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.jsoup.Jsoup
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit


// JSOUP CSS QUERIES
// CHAPTER QUERIES
private const val CHAPTERS_QUERY = "tr[style=cursor: pointer]"
private const val CHAPTER_CONTENT_QUERY = "*.chapter-content"
private const val CHAPTER_TITLE_QUERY = "*.fic-header > div > div.text-center > h1"
// COVER IMAGE
private const val COVER_IMAGE_QUERY = "img[id*=cover]"
// FAVOURITE QUERIES
private const val FAVOURITE_ITEM_QUERY = "div.fiction-list-item"             // parent element
private const val FAVOURITE_AUTHOR_QUERY = "div.fiction-info > span.author"
private const val FAVOURITE_AUTHOR_URL_QUERY = "$FAVOURITE_AUTHOR_QUERY > a"
private const val FAVOURITE_DESCRIPTION_QUERY = "div.description"
private const val FAVOURITE_NAME_QUERY = "h2.fiction-title"
private const val FAVOURITE_URL_QUERY = "$FAVOURITE_NAME_QUERY > a"
// SEARCH QUERIES
private const val SEARCH_ITEM_QUERY = "li.search-item"                      // parent element
private const val SEARCH_AUTHOR_QUERY = "div.fiction-info > span.author"
private const val SEARCH_DESCRIPTION_QUERY = "div.fiction-description"
private const val SEARCH_NAME_QUERY = "h2"
private const val SEARCH_NO_RESULT_QUERY = "h4"
private const val SEARCH_URL_QUERY = "$SEARCH_NAME_QUERY > a"
private const val SEARCH_LAST_PAGE_QUERY = "ul.pagination > li > a:contains(Last)"
// FICTION QUERIES
private const val FICTION_AUTHOR_QUERY = "h4[property=\"author\"] > span[property=\"name\"]"
private const val FICTION_AUTHOR_URL_QUERY = "$FICTION_AUTHOR_QUERY > a"
private const val FICTION_DESCRIPTION_QUERY = "div[property=\"description\"]"
private const val FICTION_NAME_QUERY = "h1[property=\"name\"]"
private const val FICTION_TAGS_QUERY = "span.tags > span.label"                      // parent element
// TAG QUERIES
private const val TAG_ATTRIBUTE = "data-tag"

internal object RoyalRoadApi : FictionProviderApi, CoroutineScope by CoroutineScope(Dispatchers.IO) {
    override val baseUrl = "https://www.royalroad.com/"

    private val service: RoyalRoadService

    init {
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.HEADERS }
        val httpClient = OkHttpClient.Builder().apply {
                        addInterceptor(logging)
            readTimeout(30, TimeUnit.SECONDS)
            followRedirects(true)
            followSslRedirects(true)
        }
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(httpClient.build())
            .build()

        service = retrofit.create(RoyalRoadService::class.java)
    }

    override suspend fun getChapter(chapterId: String): Chapter {
        val response = service.getChapter(chapterId)
        return if (response.isSuccessful) {
            val doc = Jsoup.parse(response.body()?.string())
            Chapter(
                doc.select(CHAPTER_TITLE_QUERY).text(),
                doc.select(CHAPTER_CONTENT_QUERY).html().lines()
            )
        } else Chapter()
    }

    override suspend fun getChapters(chapterIds: List<String>): List<Chapter> {
        val deferredList = coroutineScope {
            chapterIds.map { async { getChapter(it) } }
        }
        return deferredList.map { it.await() }
    }

    override suspend fun getFiction(fictionId: String): FictionMetadata {
        println("ID: $fictionId")
        val response = service.getFiction(fictionId)

        if (response.isSuccessful) {
            val doc = Jsoup.parse(response.body()?.string())
            println("ID2: $fictionId")
            val name = doc.select(FICTION_NAME_QUERY).text()
            val author = doc.select(FICTION_AUTHOR_QUERY).text()
            val authorUrl = doc.select(FICTION_AUTHOR_URL_QUERY).attr("href")
            val description = doc.select(FICTION_DESCRIPTION_QUERY).html().lines()
            val imageUrl = doc.select(COVER_IMAGE_QUERY).first().absUrl("src")
            val tags = doc.select(FICTION_TAGS_QUERY).map { it.text() }
            val chapters = doc.select(CHAPTERS_QUERY).map {
                it.attr("data-url")
            }
            println("ID: $fictionId")
            println("name: $name")
            println("author: $name")
            description.forEach {  println("Desc: $it")}
            tags.forEach {  println("Tags: $it")}
            chapters.forEach {  println("Chapters: $it")}
            return FictionMetadata(
                name = name,
                provider = baseUrl,
                fictionId = fictionId,
                author = author,
                authorId = authorUrl,
                description = description,
                imageUrl = imageUrl,
                tags = tags,
                chapters = chapters
            )
        } else throw Exception("Could not get fiction: $fictionId")
    }

    override suspend fun getFictionChapters(fictionMetadata: FictionMetadata, start: Int, end: Int): List<Chapter> =
        getChapters(fictionMetadata.chapters.subList(start, end))

    override suspend fun getTags(): List<String> {
        val response = service.getTags()
        val tagList = mutableListOf<String>()
        if (response.isSuccessful) {
            Jsoup.parse(response.body()?.string()).select("*[$TAG_ATTRIBUTE]").run {
                forEach { tag ->
                    tagList.add(tag.attr(TAG_ATTRIBUTE))
                }
            }
        }
        return tagList
    }

    @kotlinx.coroutines.ExperimentalCoroutinesApi
    override fun search(
        query: String?,
        name: String?,
        author: String?,
        tags: List<String>?
    ): ReceiveChannel<FictionMetadata> = produce {
        val response = service.search(null, query, name, author, tags)

        if (response.isSuccessful) {
            val doc = Jsoup.parse(response.body()?.string())
            val lastPageHref = doc.select(SEARCH_LAST_PAGE_QUERY).attr("href")
            println("Href: $lastPageHref")
            val lastPage = getLastResultPage(lastPageHref)
            for (resultPage in 1..lastPage) {
                getSearchResults(resultPage, query, name, author, tags).consumeEach { send(it) }
            }
        } else throw Exception("Could not execute the search on RoyalRoad.")
    }

    private fun getLastResultPage(lastPageHref: String?): Int {
        return lastPageHref?.run {
            val pageAttrRegex = """search\?page=(\d+)"""
            val pageAttr = pageAttrRegex.toRegex().find(lastPageHref)?.value ?: ""
            val numberRegex = """\d+""".toRegex()
            numberRegex.find(pageAttr)?.value?.toInt() ?: 1
        } ?: 1
    }

    @kotlinx.coroutines.ExperimentalCoroutinesApi
    private fun getSearchResults(
        page: Int,
        query: String?,
        name: String?,
        author: String?,
        tags: List<String>?
    ): ReceiveChannel<FictionMetadata> = produce {
        with(service.search(page, query, name, author, tags)) {
            if (isSuccessful) {
                val doc = Jsoup.parse(body()?.string())
                val item = doc.select(SEARCH_ITEM_QUERY)
                item.forEach { element ->
                    element.select(SEARCH_URL_QUERY).attr("href").let {
                        println("SEARCH HREF: $it")
                        send(getFiction(it.toString()))
                    }
                }
            } else throw Exception("Could not execute the search on RoyalRoad.")

        }
    }
}