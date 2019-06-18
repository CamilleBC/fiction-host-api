package me.camillebc.fictionhostapi.royalroad

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import me.camillebc.fictionhostapi.Chapter
import me.camillebc.fictionhostapi.Fiction
import me.camillebc.fictionhostapi.FictionHostApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.jsoup.Jsoup
import retrofit2.Retrofit
import java.io.File
import java.lang.Exception
import java.util.concurrent.TimeUnit


private const val BASE_URL = "https://www.royalroad.com/"
private const val HOST = "royalroad"
// JSOUP CSS QUERIES
// CHAPTER QUERIES
private const val CHAPTERS_QUERY = "tr[style=cursor: pointer]"

// COVER IMAGE
private const val COVER_IMAGE_QUERY = "img[id~=cover]"
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
// FICTION QUERIES
private const val FICTION_AUTHOR_QUERY = "h4[property=\"author\"] > span[property=\"name\"]"
private const val FICTION_AUTHOR_URL_QUERY = "$FICTION_AUTHOR_QUERY > a"
private const val FICTION_DESCRIPTION_QUERY = "div[property=\"description\"]"
private const val FICTION_NAME_QUERY = "h1[property=\"name\"]"
private const val FICTION_TAGS_QUERY = "span.tags > span.label"                      // parent element
// ID INDEXES
private const val FICTION_ID_INDEX = 2
private const val AUTHOR_ID_INDEX = 2
// TAG QUERIES
private const val TAG_ATTRIBUTE = "data-tag"

object RoyalRoadApi : FictionHostApi, CoroutineScope by CoroutineScope(Dispatchers.IO) {
    private val service: RoyalRoadService

    init {
        // TODO("clean debug") // Change or remove the Http logger
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        val httpClient = OkHttpClient.Builder().apply {
            addInterceptor(logging)
            readTimeout(30, TimeUnit.SECONDS)
            followRedirects(true)
            followSslRedirects(true)
        }
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient.build())
            .build()

        service = retrofit.create(RoyalRoadService::class.java)
    }

    override suspend fun getChapter(fictionId: String, chapterId: String): File {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getChapterRange(fictionId: String, startChapterId: String, endChapterId: String): List<File> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getFiction(fictionId: String): Fiction {
        val response = service.getFiction(fictionId)
        lateinit var fiction: Fiction

        if (response.isSuccessful) {
            val doc = Jsoup.parse(response.body()?.string())
            val name = doc.select(FICTION_NAME_QUERY).text()
            val author= doc.select(FICTION_AUTHOR_QUERY).text()
            val authorUrl = doc.select(FICTION_AUTHOR_URL_QUERY).attr("href")
            val description = StringBuilder().also {
                doc.select(FICTION_DESCRIPTION_QUERY).forEach { p -> it.appendln(p.text())
                }
            }.toString()
            val imageUrl = doc.select(COVER_IMAGE_QUERY).first().absUrl("src")
            val chapters = mutableListOf<Chapter>().apply {
                doc.select(CHAPTERS_QUERY).forEach { chapterUrl ->
                    add(Chapter(chapterUrl.attr("data-url")))
                }
            }
                fiction = Fiction(
                    name = name,
                    host = HOST,
                    hostUrl = BASE_URL + fictionId,
                    author = author,
                    authorUrl = authorUrl,
                    description = description,
                    imageUrl = imageUrl,
                    chapters = chapters
                )
            }
        else throw Exception("Could not get fiction: $fictionId")
        return fiction
    }

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

    override suspend fun search(): List<Fiction> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}