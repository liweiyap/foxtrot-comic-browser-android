package com.liweiyap.foxtrot

import com.liweiyap.foxtrot.util.StripDataModel
import com.liweiyap.foxtrot.util.scraper.ScraperResult
import com.liweiyap.foxtrot.util.scraper.WebpageScraper
import javax.inject.Inject

class ScraperRepository @Inject constructor(private val scraper: WebpageScraper){

    suspend fun scrapeLatestStripMainSafe(): ScraperResult<StripDataModel> = scraper.scrapeLatestStripMainSafe()

    suspend fun scrapePrevStripMainSafe(currentStripUrlString: String): ScraperResult<StripDataModel> = scraper.scrapePrevStripMainSafe(currentStripUrlString)

    suspend fun getStripCountMainSafe(): ScraperResult<Int> = scraper.getStripCountMainSafe()

    fun getLatestStripUrl(): String = scraper.getLatestStripUrl()

}