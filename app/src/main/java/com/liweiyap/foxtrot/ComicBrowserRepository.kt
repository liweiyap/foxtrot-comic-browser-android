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
        scrapeStripDataIfNotInDatabase(urlString)
        return@withContext stripDao.get(urlString)
    }

    private suspend fun scrapeLatestStripData() = withContext(Dispatchers.IO) {
        val latestStrip: ScraperResult<StripDataModel> = scraper.scrapeLatestStripDataMainSafe()
        if (latestStrip is ScraperResult.Success<StripDataModel>) {
            stripDao.insert(latestStrip.data)
        }
    }

    private suspend fun scrapeStripDataIfNotInDatabase(urlString: String) = withContext(Dispatchers.IO) {
        val doesStripAlreadyExist: Boolean = stripDao.hasStrip(urlString)
        if (!doesStripAlreadyExist) {
            val strip: ScraperResult<StripDataModel> = scraper.scrapeStripDataMainSafe(urlString)
            if (strip is ScraperResult.Success<StripDataModel>) {
                stripDao.insert(strip.data)
            }
        }
    }

    private fun getLatestStripUrl(): String? =
        scraper.getLatestStripUrl()

}