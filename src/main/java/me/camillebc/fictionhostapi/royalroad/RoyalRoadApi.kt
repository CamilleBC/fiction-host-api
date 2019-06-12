package me.camillebc.fictionhostapi.royalroad

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import me.camillebc.fictionhostapi.Fiction
import me.camillebc.fictionhostapi.FictionHostApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.jsoup.Jsoup
import retrofit2.Retrofit
import java.io.File
import java.util.concurrent.TimeUnit


private const val TAG_ATTRIBUTE = "data-tag"

object RoyalRoadApi : FictionHostApi, CoroutineScope by CoroutineScope(Dispatchers.IO) {
    private val service: RoyalRoadService

    init {
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        val httpClient = OkHttpClient.Builder().apply {
            addInterceptor(logging)
            readTimeout(30, TimeUnit.SECONDS)
            followRedirects(true)
            followSslRedirects(true)
        }
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.royalroad.com/")
            .client(httpClient.build())
            .build()

        service = retrofit.create(RoyalRoadService::class.java)
    }

    override suspend fun getChapter(id: String): File {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getChapterRange(start: String, end: String): List<File> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getFiction(id: String, download: Boolean): Fiction {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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