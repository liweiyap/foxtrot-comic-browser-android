package com.liweiyap.foxtrot.util.scraper

sealed class ScraperResult<out R> {
    data class Success<out T>(val data: T) : ScraperResult<T>()
    data class Error(val exception: Exception) : ScraperResult<Nothing>()
}