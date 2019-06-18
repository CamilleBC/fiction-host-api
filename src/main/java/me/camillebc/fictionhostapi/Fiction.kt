package me.camillebc.fictionhostapi

data class Fiction(
    val name: String,
    val hostUrl: String,
    val fictionId: String,
    val author: String = "unknown",
    val authorUrl: String,
    val description: String = "No description available.",
    val imageUrl: String?,
    val tags: List<String>,
    val pageCount: Long? = null,
    val ratings: Int? = null,
    val userRatings: Int? = null,
    val chapters: List<Chapter>
)