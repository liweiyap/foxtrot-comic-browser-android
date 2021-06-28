package com.liweiyap.foxtrot

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.util.*

class TestConnectionBroker {

    suspend fun makeConnectionRequest(): TestResult<String> {
        return withContext(Dispatchers.IO) {
            return@withContext scrapeLatestStrip()
        }
    }

    private fun scrapeLatestStrip(): TestResult<String> {
        val result: String

        try {
            val homePage: Document = Jsoup.connect(mHomeUrlString).get()
            val homePageStripElements: Elements = homePage.getElementsByTag("article")
            if (homePageStripElements.size < 1) {  // size should be equal to 6
                throw Exception("TestConnectionBroker::scrapeLatestStrip(): no element named 'article' on home page.")
            }
            val latestStripElement: Element = homePageStripElements.first()
            val latestStripEntry: Elements = latestStripElement.getElementsByTag("a")
            mLatestStripLink = latestStripEntry.attr("href")
            val latestStripLinkScrapeResult: TestResult<String> = scrapeStrip(mLatestStripLink)
            if (latestStripLinkScrapeResult is TestResult.Success<String>) {
                val latestStripLinkHtml: String = latestStripLinkScrapeResult.component1()
                result = latestStripLinkHtml
            } else {
                throw Exception("TestConnectionBroker::scrapeLatestStrip(): error scraping link to latest strip.")
            }
        } catch (e: Exception) {
            return TestResult.Error(e)
        }

        return TestResult.Success(result)
    }

    private fun scrapeStrip(urlString: String): TestResult<String> {
        val stripData: StripDataModel

        try {
            val stripPage: Document = Jsoup.connect(urlString).get()
            val stripEntry: Elements = stripPage.getElementsByClass("entry")
            val stripTitle: String = stripEntry.select(".entry-newtitle").text()
            val stripDateRaw: String = stripEntry.select(".entry-summary").text()
            val stripDate: StripDate = DateFormatter.formatDate(stripDateRaw)
            val stripImageMetadata: Elements = stripEntry.select(".entry-content").first().getElementsByTag("img")
            val stripImageSourceUrl: String = stripImageMetadata.attr("src")
            val stripImageAltText: String = stripImageMetadata.attr("alt")
            stripData = StripDataModel(stripTitle, stripDate, stripImageSourceUrl, stripImageAltText)
        } catch (e: Exception) {
            return TestResult.Error(e)
        }

        return TestResult.Success(stripData.imageAltText)
    }

    private val mHomeUrlString: String = "https://foxtrot.com/"
    private var mLatestStripLink: String by initOnce()
}