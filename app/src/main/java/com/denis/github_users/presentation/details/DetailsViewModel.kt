package com.denis.github_users.presentation.details

import android.content.Context
import androidx.lifecycle.ViewModel
import com.denis.github_users.R
import com.denis.github_users.domain.Converter
import com.denis.github_users.domain.Repository
import com.denis.github_users.presentation.details.model.PresentationDetails
import com.google.gson.JsonSyntaxException
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val repository: Repository,
    private val converter: Converter,
    @ApplicationContext private val context: Context,
) : ViewModel() {
    private val _stateFlow = MutableSharedFlow<LoadingState>()
    val stateFlow: Flow<LoadingState>
        get() = _stateFlow

    suspend fun startLoading(userLogin: String?) {
        if (userLogin.isNullOrEmpty()) {
            _stateFlow.emit(LoadingState.Error(context.getString(R.string.wrong_user_name)))
            return
        }
        _stateFlow.emit(LoadingState.Started)
        try {
            val userDetails = converter.convertRepositoryUserDetailsToPresentation(
                repository.loadUserDetails(userLogin)
            )
            _stateFlow.emit(LoadingState.Loaded(userDetails))
        } catch (exception: JsonSyntaxException) {
            _stateFlow.emit(LoadingState.Error(context.getString(R.string.result_of_parsing)))
        } catch (exception: IOException) {
            _stateFlow.emit(LoadingState.Error(context.getString(R.string.result_of_io)))
        } catch (exception: HttpException) {
            _stateFlow.emit(LoadingState.Error(context.getString(R.string.network_error)))
        } catch (exception: IllegalStateException) {
            _stateFlow.emit(LoadingState.Error(context.getString(R.string.no_more_elements)))
        } catch (e: Exception) {
            _stateFlow.emit(
                LoadingState.Error(
                    e.localizedMessage ?: context.getString(R.string.unknown_issue)
                )
            )
        }
    }
}

sealed class LoadingState {
    object Started : LoadingState()
    class Loaded(val userDataDetails: PresentationDetails) : LoadingState()
    class Error(val error: String) : LoadingState()
}