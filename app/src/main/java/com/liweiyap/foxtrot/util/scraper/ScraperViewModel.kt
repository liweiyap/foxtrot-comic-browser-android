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
        _stripDataResult.value = repo.scrapeLatestStripMainSafe()

        // basically the same as:
        // `var tmpStripResult: ScraperResult<StripDataModel> = repo.scrapePrevStripMainSafe(repo.getLatestStripUrl())`,
        // but with null check
        var tmpStripResult: ScraperResult<StripDataModel> = repo.getLatestStripUrl()?.let {
            repo.scrapePrevStripMainSafe(it)
        } ?: return@launch

        while (tmpStripResult is ScraperResult.Success<StripDataModel>) {
            _stripDataResult.value = tmpStripResult
            tmpStripResult = repo.scrapePrevStripMainSafe(tmpStripResult.component1().url)
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