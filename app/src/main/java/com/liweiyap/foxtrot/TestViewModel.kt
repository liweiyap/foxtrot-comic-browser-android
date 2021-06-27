package com.liweiyap.foxtrot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class TestViewModel(private val broker: TestConnectionBroker): ViewModel() {

    fun makeConnectionRequest(observer: Observer) = viewModelScope.launch {
        val result = try {
            broker.makeConnectionRequest()
        } catch (e: Exception) {
            TestResult.Error(Exception("Connection request failed"))
        }
        when (result) {
            is TestResult.Success<String> -> observer.update(result.component1())
            else -> observer.update("Fail")
        }
    }

}