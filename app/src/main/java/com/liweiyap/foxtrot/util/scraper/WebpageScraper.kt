package com.liweiyap.foxtrot.util.scraper

import com.liweiyap.foxtrot.util.StripDataModel
import com.liweiyap.foxtrot.util.StripDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.util.*
import javax.inject.Inject

class WebpageScraper @Inject constructor() {

    suspend fun scrapeLatestStripMainSafe(): ScraperResult<StripDataModel> {
        return withContext(Dispatchers.IO) {
            scrapeLatestStrip()
        }
    }

    suspend fun scrapePrevStripMainSafe(currentStripUrlString: String): ScraperResult<StripDataModel> {
        return withContext(Dispatchers.IO) {
            scrapePrevStrip(currentStripUrlString)
        }
    }

    suspend fun getStripCountMainSafe(): ScraperResult<Int> {
        return withContext(Dispatchers.IO) {
            getStripCount()
        }
    }

    private fun scrapeLatestStrip(): ScraperResult<StripDataModel> {
        val strip: StripDataModel

        try {
            val homePage: Document = Jsoup.connect(mHomeUrlString).timeout(mConnectionTimeoutInMilliSecs).get()
            val homePageStripElements: Elements = homePage.getElementsByTag("article")
            if (homePageStripElements.size < 1) {  // size should be equal to 6
                throw Exception("WebpageScraper::scrapeLatestStrip(): no element named 'article' on home page.")
            }
            val latestStripElement: Element = homePageStripElements.first()
            val latestStripEntry: Elements = latestStripElement.getElementsByTag("a")
            mLatestStripUrlString = latestStripEntry.attr("href")  // if attr does not exist, `.attr()` returns an empty String
            val latestStripLinkScrapeResult = scrapeStrip(mLatestStripUrlString)
            if (latestStripLinkScrapeResult is ScraperResult.Success<StripDataModel>) {
                strip = latestStripLinkScrapeResult.component1()
            } else {
                throw Exception("WebpageScraper::scrapeLatestStrip(): error scraping link to latest strip.")
            }
        } catch (e: Exception) {
            return ScraperResult.Error(e)
        }

        return ScraperResult.Success(strip)
    }

    private fun scrapePrevStrip(currentStripUrlString: String): ScraperResult<StripDataModel> {
        val strip: StripDataModel

        try {
            val currentStripPage: Document = Jsoup.connect(currentStripUrlString).timeout(mConnectionTimeoutInMilliSecs).get()
            val currentStripEntry: Elements = currentStripPage.getElementsByClass("entry")
            val adjacentStripEntries: Elements = currentStripEntry.select(".entry-navarrows").select("[rel=\"prev\"]")
            if (adjacentStripEntries.size < 1) {
                throw Exception("WebpageScraper::scrapePrevStrip(): Strip with URL $currentStripUrlString does not have a previous strip.")
            }
            val prevStripEntry: Elements = adjacentStripEntries.first().getElementsByTag("a")
            val prevStripLink: String = prevStripEntry.attr("href")
            val prevStripLinkScrapeResult = scrapeStrip(prevStripLink)
            if (prevStripLinkScrapeResult is ScraperResult.Success<StripDataModel>) {
                strip = prevStripLinkScrapeResult.component1()
            } else {
                throw Exception("WebpageScraper::scrapePrevStrip(): error scraping link to previous strip.")
            }
        } catch (e: Exception) {
            return ScraperResult.Error(e)
        }

        return ScraperResult.Success(strip)
    }

    private fun scrapeStrip(urlString: String): ScraperResult<StripDataModel> {
        val stripData: StripDataModel

        try {
            val stripPage: Document = Jsoup.connect(urlString).timeout(mConnectionTimeoutInMilliSecs).get()
            val stripEntry: Elements = stripPage.getElementsByClass("entry")
            val stripTitle: String = stripEntry.select(".entry-newtitle").text()
            val stripDateRaw: String = stripEntry.select(".entry-summary").text()
            val stripDate: StripDate = DateFormatter.formatDate(stripDateRaw)
                ?: throw Exception("WebpageScraper::scrapeStrip(): error retrieving date of strip from $stripDateRaw.")
            val stripImageMetadata: Elements = stripEntry.select(".entry-content").first().getElementsByTag("img")
            val stripImageSourceUrl: String = stripImageMetadata.attr("src")
            val stripImageAltText: String = stripImageMetadata.attr("alt")
            val stripTagsRaw: Elements = stripEntry.select(".entry-tags")
            val stripTags: ArrayList<String> = arrayListOf()
            if (stripTagsRaw.size >= 1) {
                for (rawTag in stripTagsRaw.first().getElementsByTag("a")) {
                    stripTags.add(rawTag.text())
                }
            }
            stripData = StripDataModel(urlString, stripTitle, stripDate, stripImageSourceUrl, stripImageAltText, stripTags)
        } catch (e: Exception) {
            return ScraperResult.Error(e)
        }

        return ScraperResult.Success(stripData)
    }

    private fun getStripCount(): ScraperResult<Int> {
        val numberOfStrips: Int
        val stripsPerPage = 6

        try {
            val homePage: Document = Jsoup.connect(mHomeUrlString).timeout(mConnectionTimeoutInMilliSecs).get()
            val pageNavigator: Elements = homePage.getElementsByClass("navigation")
            val pageNumbers: Elements = pageNavigator.select(".page-numbers")
            val lastPageNumber: Element = pageNumbers[pageNumbers.size - 2]
            val numberOfPages: Int = Integer.valueOf(lastPageNumber.text())
            val lastPageAttr: Elements = lastPageNumber.getElementsByTag("a")
            val lastPageUrl: String = lastPageAttr.attr("href")
            val lastPage: Document = Jsoup.connect(lastPageUrl).timeout(mConnectionTimeoutInMilliSecs).get()
            val lastPageStripElements: Elements = lastPage.getElementsByTag("article")
            numberOfStrips = (numberOfPages - 1) * stripsPerPage + lastPageStripElements.size
            if (numberOfStrips < 0) {
                throw Exception("WebpageScraper::getNumberOfStrips(): no. of strips cannot be negative.")
            }
        } catch (e: Exception) {
            return ScraperResult.Error(e)
        }

        return ScraperResult.Success(numberOfStrips)
    }

    fun getLatestStripUrl(): String? {
        if (!this::mLatestStripUrlString.isInitialized) {
            return null
        }
        return mLatestStripUrlString
    }

    private val mHomeUrlString: String = "https://foxtrot.com/"
    private lateinit var mLatestStripUrlString: String

    private val mConnectionTimeoutInMilliSecs: Int = 15000
}