package me.camillebc.fictionproviderapi

import me.camillebc.fictionproviderapi.royalroad.RoyalRoadApi

enum class FictionProvider(val host: String) {
    ROYALROAD(RoyalRoadApi.baseUrl)
}
