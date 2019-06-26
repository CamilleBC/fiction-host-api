package me.camillebc.fictionproviderapi

data class FictionMetadata(
    val name: String,
    val provider: String,
    val fictionId: String,
    val author: String = "unknown",
    val authorId: String? = null,
    var description: List<String>,
    var imageUrl: String? = null,
    var tags: List<String>,
    var pageCount: Long? = null,
    var ratings: Int? = null,
    var userRatings: Int? = null,
    var chapters: List<String> = listOf()
)