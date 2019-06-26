package me.camillebc.fictionproviderapi

import me.camillebc.fictionproviderapi.royalroad.RoyalRoadApi

object ApiProvider {
    fun getApi(host: FictionProvider): FictionProviderApi = when (host) {
       FictionProvider.ROYALROAD -> RoyalRoadApi
    }
}