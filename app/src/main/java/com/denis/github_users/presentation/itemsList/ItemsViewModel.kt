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
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class ItemsViewModel @Inject constructor(
    repository: Repository,
    converter: Converter,
) : ViewModel() {

    @OptIn(ExperimentalPagingApi::class)
    val itemsFlow: Flow<PagingData<PresentationUserData>> =
        flow { emitAll(repository.loadItem(1)) }
            .map { pagingData -> pagingData.map { converter.convertFromEntityToPresentation(it) } }
            .cachedIn(viewModelScope)
}
