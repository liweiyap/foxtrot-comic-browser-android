package com.liweiyap.foxtrot

import androidx.lifecycle.*
import com.liweiyap.foxtrot.database.StripDataModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ComicBrowserViewModel @Inject constructor(private val repo: ComicBrowserRepository): ViewModel() {

    fun fetchLatestStripData() = viewModelScope.launch {
        _displayedStripDataResult.value = repo.fetchLatestStripData()
    }

    fun fetchStripData(urlString: String) = viewModelScope.launch {
        _displayedStripDataResult.value = repo.fetchStripData(urlString)
    }

    fun fetchAllStripData() = viewModelScope.launch {
        _loadingStripDataResult.value = repo.fetchLatestStripData()

        while (_loadingStripDataResult.value != null) {
            if (_loadingStripDataResult.value!!.prevStripUrl == null) {
                _loadingStripDataResult.value = null
                continue
            }

            _loadingStripDataResult.value = repo.fetchStripData(_loadingStripDataResult.value!!.prevStripUrl!!)
        }
    }

    fun countStrips() = viewModelScope.launch {
        _stripCountResult.value = repo.getStripCount()
    }

    private val _loadingStripDataResult = MutableLiveData<StripDataModel?>()
    val loadingStripDataResult: LiveData<StripDataModel?>
        get() = _loadingStripDataResult

    private val _displayedStripDataResult = MutableLiveData<StripDataModel?>()
    val displayedStripDataResult: LiveData<StripDataModel?>
        get() = _displayedStripDataResult

    private val _stripCountResult = MutableLiveData<Int?>()
    val stripCountResult: LiveData<Int?>
        get() = _stripCountResult

    val databaseSize: LiveData<Int> = repo.getDatabaseSize().asLiveData()
}