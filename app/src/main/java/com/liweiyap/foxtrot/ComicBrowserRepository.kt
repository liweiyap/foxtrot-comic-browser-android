package com.liweiyap.foxtrot

import com.liweiyap.foxtrot.database.StripDao
import com.liweiyap.foxtrot.database.StripDataModel
import com.liweiyap.foxtrot.scraper.ScraperResult
import com.liweiyap.foxtrot.scraper.WebpageScraper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ComicBrowserRepository @Inject constructor(private val scraper: WebpageScraper, private val stripDao: StripDao) {

    suspend fun fetchLatestStripData(): StripDataModel? = withContext(Dispatchers.IO) {
        scrapeLatestStripData()
        return@withContext getLatestStripUrl()?.let {
            stripDao.get(it)
        }
    }

    suspend fun fetchStripData(urlString: String): StripDataModel? = withContext(Dispatchers.IO) {
        scrapeStripData(urlString)
        return@withContext stripDao.get(urlString)
    }

    private suspend fun scrapeLatestStripData() = withContext(Dispatchers.IO) {
        val latestStrip: ScraperResult<StripDataModel> = scraper.scrapeLatestStripDataMainSafe()
        if (latestStrip is ScraperResult.Success<StripDataModel>) {
            stripDao.insert(latestStrip.data)
        }
    }

    private suspend fun scrapeStripData(urlString: String) = withContext(Dispatchers.IO) {
        if ( (stripDao.hasStrip(urlString)) && (stripDao.countLatest() == 1) ) {
            return@withContext
        }

        val strip: ScraperResult<StripDataModel> = scraper.scrapeStripDataMainSafe(urlString)
        if (strip is ScraperResult.Success<StripDataModel>) {
            stripDao.insert(strip.data)
        }
    }

    private fun getLatestStripUrl(): String? =
        scraper.getLatestStripUrl()

}