package com.liweiyap.foxtrot.scraper

import com.liweiyap.foxtrot.database.StripDataModel
import com.liweiyap.foxtrot.util.DateFormatter
import com.liweiyap.foxtrot.util.StringParser
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
            scrapeStripData(StringParser.secureWebProtocol(urlString))
        }
    }

    suspend fun getStripCountMainSafe(): ScraperResult<Int> {
        return withContext(Dispatchers.IO) {
            getStripCount()
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
            mLatestStripUrlString = StringParser.secureWebProtocol(latestStripEntry.attr("href"))  // if attr does not exist, `.attr()` returns an empty String
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
            val stripTitle: String = StringParser.standardiseUnicodeApostrophe(stripEntry.select(".entry-newtitle").text())

            // date
            val stripDateRaw: String = stripEntry.select(".entry-summary").text()
            val stripDate: StripDate = DateFormatter.formatDate(stripDateRaw)
                ?: throw Exception("WebpageScraper::scrapeStrip(): error retrieving date of strip from $stripDateRaw.")

            // image source URL
            val stripImageMetadata: Elements = stripEntry.select(".entry-content").first().getElementsByTag("img")
            val stripImageSourceUrl: String = StringParser.secureWebProtocol(stripImageMetadata.attr("src"))

            // image alt text
            val stripImageAltText: String = StringParser.standardiseUnicodeApostrophe(stripImageMetadata.attr("alt"))

            // tags
            val stripTagsRaw: Elements = stripEntry.select(".entry-tags")
            val stripTags: ArrayList<String> = arrayListOf()
            if (stripTagsRaw.size >= 1) {
                for (rawTag in stripTagsRaw.first().getElementsByTag("a")) {
                    stripTags.add(rawTag.text())
                }
            }

            // URL of previous strip
            val adjacentStripEntries: Elements = stripEntry.select(".entry-navarrows")
            val prevStripEntries: Elements = adjacentStripEntries.select("[rel=\"prev\"]")
            val prevStripUrl: String? = if (prevStripEntries.size >= 1) {
                val prevStripEntry: Elements = prevStripEntries.first().getElementsByTag("a")
                StringParser.secureWebProtocol(prevStripEntry.attr("href"))
            } else {
                null
            }

            // URL of next strip
            val nextStripEntries: Elements = adjacentStripEntries.select("[rel=\"next\"]")
            val nextStripUrl: String? = if (nextStripEntries.size >= 1) {
                val nextStripEntry: Elements = nextStripEntries.first().getElementsByTag("a")
                StringParser.secureWebProtocol(nextStripEntry.attr("href"))
            } else {
                null
            }

            // store in instance of data class
            stripData = StripDataModel(urlString, stripTitle, stripDate, stripImageSourceUrl, stripImageAltText, stripTags, prevStripUrl, nextStripUrl)
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
            val lastPageUrl: String = StringParser.secureWebProtocol(lastPageAttr.attr("href"))
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