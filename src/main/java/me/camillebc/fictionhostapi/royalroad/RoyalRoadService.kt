package me.camillebc.fictionhostapi.royalroad

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

internal interface RoyalRoadService {
    @GET("fiction/{fictionId}/chapter/{chapterId}")
    suspend fun getChapter(@Path("fictionId") fictionId: String, @Path("chapterId") chapterId: String): Response<ResponseBody>

    @GET("fiction/{fictionId}")
    suspend fun getFiction(@Path("fictionId") fictionId: String): Response<ResponseBody>

    @GET("fictions/search?advanced=True")
    suspend fun getTags(): Response<ResponseBody>

    @GET("fictions/search")
    suspend fun search(
        @Query("keyword") keyword: String,
        @Query("name") title: String?,
        @Query("author") author: String?,
        @Query("tagsAdd") tags: Array<String>?
    ): Response<Body>
}