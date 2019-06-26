package me.camillebc.fictionproviderapi.royalroad

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

internal interface RoyalRoadService {
    @GET("{chapterId}")
    suspend fun getChapter(@Path("chapterId", encoded = true) chapterId: String): Response<ResponseBody>

    @GET("{fictionId}")
    suspend fun getFiction(@Path("fictionId", encoded = true) fictionId: String): Response<ResponseBody>

    @GET("{searchResult}")
    suspend fun getSearchResult(@Path("searchResult") searchResult: String): Response<ResponseBody>

    @GET("fictions/search?advanced=True")
    suspend fun getTags(): Response<ResponseBody>

    @GET("fictions/search")
    suspend fun search(
        @Query("page") page: Int? = null,
        @Query("keyword") keyword: String? = null,
        @Query("name") title: String? = null,
        @Query("author") author: String? = null,
        @Query("tagsAdd") tags: List<String>? = null
    ): Response<ResponseBody>
}