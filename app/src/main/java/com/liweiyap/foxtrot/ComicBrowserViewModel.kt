package com.liweiyap.foxtrot

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.liweiyap.foxtrot.util.database.StripDataModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ComicBrowserViewModel @Inject constructor(private val repo: ComicBrowserRepository): ViewModel() {

    fun scrapeAllStrips() = viewModelScope.launch {
        var tmpStripResult: StripDataModel? = repo.fetchLatestStripData()

        while (tmpStripResult != null) {
            _stripDataResult.value = tmpStripResult

            tmpStripResult = tmpStripResult.prevStripUrl?.let {
                repo.fetchStripData(it)
            }
        }
    }

    private val _stripDataResult = MutableLiveData<StripDataModel?>()
    val stripDataResult: LiveData<StripDataModel?>
        get() = _stripDataResult
}