package com.liweiyap.foxtrot

import androidx.lifecycle.*
import com.liweiyap.foxtrot.database.StripDataModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ComicBrowserViewModel @Inject constructor(private val repo: ComicBrowserRepository): ViewModel() {

    fun fetchAllStripData() = viewModelScope.launch {
        _fetchingStripDataResult.value = repo.fetchLatestStripData()

        while (_fetchingStripDataResult.value != null) {
            if (_fetchingStripDataResult.value!!.prevStripUrl == null) {
                _fetchingStripDataResult.value = null
                continue
            }

            _fetchingStripDataResult.value = repo.fetchStripData(_fetchingStripDataResult.value!!.prevStripUrl!!)
        }
    }

    fun countStrips() = viewModelScope.launch {
        _stripCountResult.value = repo.getStripCount()
    }

    fun toggleIsFavourite(urlString: String) = viewModelScope.launch {
        repo.toggleIsFavourite(urlString)
    }

    private val _fetchingStripDataResult = MutableLiveData<StripDataModel?>()
    val fetchingStripDataResult: LiveData<StripDataModel?>
        get() = _fetchingStripDataResult

    private val _stripCountResult = MutableLiveData<Int?>()
    val stripCountResult: LiveData<Int?>
        get() = _stripCountResult

    val stripDatabase: LiveData<List<StripDataModel>> = repo.getAllStrips().asLiveData()
}