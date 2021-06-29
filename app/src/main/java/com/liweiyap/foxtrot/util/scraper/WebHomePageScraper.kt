package com.liweiyap.foxtrot.util.scraper

import com.liweiyap.foxtrot.util.StripDataModel
import com.liweiyap.foxtrot.util.StripDate
import com.liweiyap.foxtrot.util.initOnce
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.util.*

class WebHomePageScraper {

    suspend fun scrapLatestStripThreadSafe(): ScraperResult<StripDataModel> {
        return withContext(Dispatchers.IO) {
            return@withContext scrapeLatestStrip()
        }
    }

    private fun scrapeLatestStrip(): ScraperResult<StripDataModel> {
        val strip: StripDataModel

        try {
            val homePage: Document = Jsoup.connect(mHomeUrlString).get()
            val homePageStripElements: Elements = homePage.getElementsByTag("article")
            if (homePageStripElements.size < 1) {  // size should be equal to 6
                throw Exception("TestConnectionBroker::scrapeLatestStrip(): no element named 'article' on home page.")
            }
            val latestStripElement: Element = homePageStripElements.first()
            val latestStripEntry: Elements = latestStripElement.getElementsByTag("a")
            mLatestStripLink = latestStripEntry.attr("href")
            val latestStripLinkScrapeResult = scrapeStrip(mLatestStripLink)
            if (latestStripLinkScrapeResult is ScraperResult.Success<StripDataModel>) {
                strip = latestStripLinkScrapeResult.component1()
            } else {
                throw Exception("TestConnectionBroker::scrapeLatestStrip(): error scraping link to latest strip.")
            }
        } catch (e: Exception) {
            return ScraperResult.Error(e)
        }

        return ScraperResult.Success(strip)
    }

    private fun scrapeStrip(urlString: String): ScraperResult<StripDataModel> {
        val stripData: StripDataModel

        try {
            val stripPage: Document = Jsoup.connect(urlString).get()
            val stripEntry: Elements = stripPage.getElementsByClass("entry")
            val stripTitle: String = stripEntry.select(".entry-newtitle").text()
            val stripDateRaw: String = stripEntry.select(".entry-summary").text()
            val stripDate: StripDate = DateFormatter.formatDate(stripDateRaw)
                ?: throw Exception("TestConnectionBroker::scrapeStrip(): error retrieving date of strip.")
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
            stripData = StripDataModel(stripTitle, stripDate, stripImageSourceUrl, stripImageAltText, stripTags)
        } catch (e: Exception) {
            return ScraperResult.Error(e)
        }

        return ScraperResult.Success(stripData)
    }

    private val mHomeUrlString: String = "https://foxtrot.com/"
    private var mLatestStripLink: String by initOnce()
}