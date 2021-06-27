package com.liweiyap.foxtrot

sealed class TestResult<out R> {
    data class Success<out T>(val data: T) : TestResult<T>()
    data class Error(val exception: Exception) : TestResult<Nothing>()
}