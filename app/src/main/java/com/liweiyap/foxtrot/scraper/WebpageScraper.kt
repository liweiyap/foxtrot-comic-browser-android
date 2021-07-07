package com.liweiyap.foxtrot.scraper

import com.liweiyap.foxtrot.database.StripDataModel
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

    suspend fun scrapeLatestStripDataMainSafe(): ScraperResult<StripDataModel> {
        return withContext(Dispatchers.IO) {
            scrapeLatestStripData()
        }
    }

    suspend fun scrapeStripDataMainSafe(urlString: String): ScraperResult<StripDataModel> {
        return withContext(Dispatchers.IO) {
            scrapeStripData(urlString)
        }
    }

    private fun scrapeLatestStripData(): ScraperResult<StripDataModel> {
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
            val latestStripLinkScrapeResult = scrapeStripData(mLatestStripUrlString)
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

    private fun scrapeStripData(urlString: String): ScraperResult<StripDataModel> {
        val stripData: StripDataModel

        try {
            // establish connection
            val stripPage: Document = Jsoup.connect(urlString).timeout(mConnectionTimeoutInMilliSecs).get()

            // title
            val stripEntry: Elements = stripPage.getElementsByClass("entry")
            val stripTitle: String = stripEntry.select(".entry-newtitle").text()

            // date
            val stripDateRaw: String = stripEntry.select(".entry-summary").text()
            val stripDate: StripDate = DateFormatter.formatDate(stripDateRaw)
                ?: throw Exception("WebpageScraper::scrapeStrip(): error retrieving date of strip from $stripDateRaw.")

            // image source URL
            val stripImageMetadata: Elements = stripEntry.select(".entry-content").first().getElementsByTag("img")
            val stripImageSourceUrl: String = stripImageMetadata.attr("src")

            // image alt text
            val stripImageAltText: String = stripImageMetadata.attr("alt")

            // tags
            val stripTagsRaw: Elements = stripEntry.select(".entry-tags")
            val stripTags: ArrayList<String> = arrayListOf()
            if (stripTagsRaw.size >= 1) {
                for (rawTag in stripTagsRaw.first().getElementsByTag("a")) {
                    stripTags.add(rawTag.text())
                }
            }

            // URL of previous strip
            val adjacentStripEntries: Elements = stripEntry.select(".entry-navarrows").select("[rel=\"prev\"]")
            val prevStripUrl: String? = if (adjacentStripEntries.size >= 1) {
                val prevStripEntry: Elements = adjacentStripEntries.first().getElementsByTag("a")
                prevStripEntry.attr("href")
            } else {
                null
            }

            // store in instance of data class
            stripData = StripDataModel(urlString, stripTitle, stripDate, stripImageSourceUrl, stripImageAltText, stripTags, prevStripUrl)
        } catch (e: Exception) {
            return ScraperResult.Error(e)
        }

        return ScraperResult.Success(stripData)
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