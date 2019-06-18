package me.camillebc.fictionhostapi

data class Chapter(
    val id: String,
    var title: String? = null,
    var content: List<String>? = null
)
