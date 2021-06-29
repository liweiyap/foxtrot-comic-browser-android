package com.liweiyap.foxtrot.util.scraper

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.liweiyap.foxtrot.util.StripDataModel
import kotlinx.coroutines.launch

class ScraperViewModel(private val scraper: WebpageScraper): ViewModel() {

    fun scrapeLatestStrip(observer: ScraperObserver) = viewModelScope.launch {
        val result = try {
            scraper.scrapeLatestStripMainSafe()
        } catch (e: Exception) {
            if (e.message == null) {
                ScraperResult.Error(Exception("ScraperViewModel::scrapeLatestStrip(): Failed to scrape latest strip."))
            } else {
                ScraperResult.Error(Exception(e.message))
            }
        }

        when (result) {
            is ScraperResult.Success<StripDataModel> -> observer.update(result.component1().title)
            else -> observer.update("Fail")
        }
    }

    fun scrapePrevStrip(observer: ScraperObserver) = viewModelScope.launch {
        val result = try {
            scraper.scrapePrevStripMainSafe(scraper.getLatestStripUrl())
        } catch (e: Exception) {
            if (e.message == null) {
                ScraperResult.Error(Exception("ScraperViewModel::scrapePrevStrip(): Failed to scrape previous strip."))
            } else {
                ScraperResult.Error(Exception(e.message))
            }
        }

        when (result) {
            is ScraperResult.Success<StripDataModel> -> observer.update(result.component1().title)
            else -> observer.update("Fail")
        }
    }

    fun scrapePrevStrips(observer: ScraperObserver) = viewModelScope.launch {
        var result = try {
            scraper.scrapePrevStripMainSafe(scraper.getLatestStripUrl())
        } catch (e: Exception) {
            if (e.message == null) {
                ScraperResult.Error(Exception("ScraperViewModel::scrapePrevStrip(): Failed to scrape previous strip."))
            } else {
                ScraperResult.Error(Exception(e.message))
            }
        }

        while (result is ScraperResult.Success<StripDataModel>) {
            observer.update(result.component1().title)
            result = try {
                scraper.scrapePrevStripMainSafe(result.component1().url)
            } catch (e: Exception) {
                if (e.message == null) {
                    ScraperResult.Error(Exception("ScraperViewModel::scrapePrevStrip(): Failed to scrape previous strip."))
                } else {
                    ScraperResult.Error(Exception(e.message))
                }
            }
        }
    }

}