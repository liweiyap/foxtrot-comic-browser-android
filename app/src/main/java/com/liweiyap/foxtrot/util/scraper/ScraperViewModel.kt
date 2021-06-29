package com.liweiyap.foxtrot.util.scraper

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.liweiyap.foxtrot.util.StripDataModel
import kotlinx.coroutines.launch

class ScraperViewModel(private val scraper: WebHomePageScraper): ViewModel() {

    fun scrapeLatestStrip(observer: ScraperObserver) = viewModelScope.launch {
        val result = try {
            scraper.scrapLatestStripThreadSafe()
        } catch (e: Exception) {
            if (e.message == null) {
                ScraperResult.Error(Exception("Connection request failed"))
            } else {
                ScraperResult.Error(Exception(e.message))
            }
        }

        when (result) {
            is ScraperResult.Success<StripDataModel> -> observer.update(result.component1().title)
            else -> observer.update("Fail")
        }
    }

}