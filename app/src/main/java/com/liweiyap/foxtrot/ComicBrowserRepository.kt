package com.liweiyap.foxtrot

import com.liweiyap.foxtrot.database.StripDao
import com.liweiyap.foxtrot.database.StripDataModel
import com.liweiyap.foxtrot.scraper.ScraperResult
import com.liweiyap.foxtrot.scraper.WebpageScraper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ComicBrowserRepository @Inject constructor(
    private val scraper: WebpageScraper,
    private val stripDao: StripDao
) {
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

    suspend fun getStripCount(): Int? = withContext(Dispatchers.IO) {
        val stripCount: ScraperResult<Int> = scraper.getStripCountMainSafe()
        if (stripCount is ScraperResult.Success<Int>) {
            return@withContext stripCount.data
        } else {
            return@withContext null
        }
    }

    fun getAllStrips(): Flow<List<StripDataModel>> =
        stripDao.getAll().distinctUntilChanged()

    suspend fun toggleIsFavourite(urlString: String) = withContext(Dispatchers.IO) {
        stripDao.toggleIsFavourite(urlString)
    }

    private suspend fun scrapeLatestStripData() = withContext(Dispatchers.IO) {
        val latestStrip: ScraperResult<StripDataModel> = scraper.scrapeLatestStripDataMainSafe()
        if (latestStrip is ScraperResult.Success<StripDataModel>) {
            store(latestStrip.data)
        }
    }

    private suspend fun scrapeStripData(urlString: String) = withContext(Dispatchers.IO) {
        if ( (stripDao.hasStrip(urlString)) && (stripDao.countLatest() == 1) ) {
            return@withContext
        }

        val strip: ScraperResult<StripDataModel> = scraper.scrapeStripDataMainSafe(urlString)
        if (strip is ScraperResult.Success<StripDataModel>) {
            store(strip.data)
        }
    }

    private suspend fun store(strip: StripDataModel) = withContext(Dispatchers.IO) {
        val dbStrip: StripDataModel? = stripDao.get(strip.url)
        if (dbStrip != null) {
            strip.isFavourite = dbStrip.isFavourite
        }
        stripDao.insert(strip)
    }

    private fun getLatestStripUrl(): String? =
        scraper.getLatestStripUrl()

}