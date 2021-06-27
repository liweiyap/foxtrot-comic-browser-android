package com.liweiyap.foxtrot

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

class TestConnectionBroker {
    private val mHomeUrlString: String = "https://foxtrot.com/"
    private lateinit var mLatestStripLink: String

    suspend fun makeConnectionRequest(): TestResult<String> {
        return withContext(Dispatchers.IO) {
            return@withContext scrapeLatestStrip()
        }
    }

    private fun scrapeLatestStrip(): TestResult<String> {
        val result: String

        try {
            val document: Document = Jsoup.connect(mHomeUrlString).get()
            val homePageStripElements: Elements = document.getElementsByTag("article")
            if (homePageStripElements.size < 1) {
                throw Exception("TestConnectionBroker::scrapeLatestStrip(): no element named 'article' on home page.")
            }
            val latestStripElement: Element = homePageStripElements[0]
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
        val document: Document
        try {
            document = Jsoup.connect(urlString).get()
        } catch (e: Exception) {
            return TestResult.Error(e)
        }
        return TestResult.Success(document.toString())
    }

}