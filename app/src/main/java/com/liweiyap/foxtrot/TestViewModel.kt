package com.liweiyap.foxtrot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class TestViewModel(private val broker: TestConnectionBroker): ViewModel() {

    fun makeConnectionRequest(observer: Observer) = viewModelScope.launch {
        val result = try {
            broker.makeConnectionRequest()
        } catch (e: Exception) {
            if (e.message == null) {
                TestResult.Error(Exception("Connection request failed"))
            } else {
                TestResult.Error(Exception(e.message))
            }
        }
        when (result) {
            is TestResult.Success<String> -> observer.update(result.component1())
            else -> observer.update("Fail")
        }
    }

}