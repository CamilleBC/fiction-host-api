package me.camillebc.fictionproviderapi

import me.camillebc.fictionproviderapi.royalroad.RoyalRoadApi

object ApiProvider {
    fun getApi(host: FictionProvider): FictionProviderApi = when (host) {
       FictionProvider.ROYALROAD -> RoyalRoadApi
    }

    fun getAllApi(): List<FictionProviderApi> = mutableListOf<FictionProviderApi>().apply {
        FictionProvider.values().map { add(getApi(it)) } }
}