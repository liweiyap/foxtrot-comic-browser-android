package com.liweiyap.foxtrot.util.scraper

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.liweiyap.foxtrot.util.StripDataModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScraperViewModel @Inject constructor(private val repo: ScraperRepository): ViewModel() {

    fun scrapeAllStrips() = viewModelScope.launch {
        var tmpStripResult: ScraperResult<StripDataModel> = repo.scrapeLatestStripMainSafe()

        while (tmpStripResult is ScraperResult.Success<StripDataModel>) {
            _stripDataResult.value = tmpStripResult

            // basically the same as:
            // `tmpStripResult = repo.scrapeStripMainSafe(tmpStripResult.data.prevStripUrl)`
            // but with null check
            tmpStripResult = tmpStripResult.data.prevStripUrl?.let {
                repo.scrapeStripMainSafe(it)
            } ?: ScraperResult.Error(Exception())
        }
    }

    fun countStrips() = viewModelScope.launch {
        _stripCountResult.value = repo.getStripCountMainSafe()
    }

    private val _stripDataResult = MutableLiveData<ScraperResult<StripDataModel>>()
    val stripDataResult: LiveData<ScraperResult<StripDataModel>>
        get() = _stripDataResult

    private val _stripCountResult = MutableLiveData<ScraperResult<Int>>()
    val stripCountResult: LiveData<ScraperResult<Int>>
        get() = _stripCountResult
}