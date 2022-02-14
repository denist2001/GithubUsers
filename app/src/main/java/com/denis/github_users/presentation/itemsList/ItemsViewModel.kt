package com.denis.github_users.presentation.itemsList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.denis.github_users.domain.Converter
import com.denis.github_users.domain.Repository
import com.denis.github_users.presentation.itemsList.model.PresentationUserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ItemsViewModel @Inject constructor(
    private val repository: Repository,
    private val converter: Converter,
) : ViewModel() {
    private val _itemsFlow = MutableSharedFlow<PagingData<PresentationUserData>>()
    val itemsFlow: Flow<PagingData<PresentationUserData>>
        get() = _itemsFlow

    init {
        viewModelScope.launch {
            startLoading()
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    private suspend fun startLoading() {
        repository.loadItem(1).cachedIn(viewModelScope).collect { pagingData ->
            val convertedPagingData = pagingData.map { userDataEntity ->
                converter.convertFromEntityToPresentation(userDataEntity)
            }
            _itemsFlow.emit(convertedPagingData)
        }
    }
}