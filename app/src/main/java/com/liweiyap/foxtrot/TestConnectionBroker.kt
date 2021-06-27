package com.liweiyap.foxtrot

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class TestConnectionBroker {
    private val urlString: String = "https://foxtrot.com/"

    suspend fun makeConnectionRequest(): TestResult<String> {
        return withContext(Dispatchers.IO) {
            return@withContext test()
        }
    }

    private fun test(): TestResult<String> {
        val document: Document
        try {
            document = Jsoup.connect(urlString).get()
        } catch (e: Exception) {
            return TestResult.Error(e)
        }
        return TestResult.Success(document.toString())
    }

}