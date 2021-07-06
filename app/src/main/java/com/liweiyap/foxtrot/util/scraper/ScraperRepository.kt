package com.liweiyap.foxtrot.util.scraper

import com.liweiyap.foxtrot.util.StripDao
import com.liweiyap.foxtrot.util.StripDataModel
import javax.inject.Inject

class ScraperRepository @Inject constructor(private val scraper: WebpageScraper, private val stripDao: StripDao){

    suspend fun scrapeLatestStripMainSafe(): ScraperResult<StripDataModel> =
        scraper.scrapeLatestStripMainSafe()

    suspend fun scrapeStripMainSafe(urlString: String): ScraperResult<StripDataModel> =
        scraper.scrapeStripMainSafe(urlString)

    suspend fun getStripCountMainSafe(): ScraperResult<Int> =
        scraper.getStripCountMainSafe()

    fun getLatestStripUrl(): String? =
        scraper.getLatestStripUrl()

}