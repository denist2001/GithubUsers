package com.denis.github_users.presentation.details

import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import coil.load
import coil.size.Scale
import coil.transform.RoundedCornersTransformation
import com.denis.github_users.R
import com.denis.github_users.databinding.DetailsFragmentBinding
import com.denis.github_users.presentation.details.model.PresentationDetails
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailsFragment : Fragment(R.layout.details_fragment) {

    private var _binding: DetailsFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<DetailsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userLogin = requireArguments().getString("login")
        _binding = DetailsFragmentBinding.bind(view)
        lifecycleScope.launch {
            subscribeOnStateChanges()
        }
        lifecycleScope.launch {
            viewModel.startLoading(userLogin)
        }
    }

    private suspend fun subscribeOnStateChanges() {
        viewModel.stateFlow.collectLatest { state ->
            when (state) {
                is LoadingState.Error -> manageErrorState(state.error)
                is LoadingState.Loaded -> manageLoadedState(state.userDataDetails)
                LoadingState.Started -> manageStartedState()
            }
        }
    }

    private fun manageStartedState() {
        binding.progressPb.visibility = VISIBLE
    }

    private fun manageLoadedState(userDataDetails: PresentationDetails) {
        binding.progressPb.visibility = GONE
        presentUserData(userDataDetails)
    }

    private fun presentUserData(userDataDetails: PresentationDetails) {
        binding.apply {
            pictureIv.load(userDataDetails.avatar_url) {
                scale(Scale.FILL)
                val rootView = binding.root.rootView
                if (rootView.measuredWidth > 0) size(
                    rootView.measuredWidth,
                    rootView.measuredWidth
                )
                placeholder(R.drawable.person_150)
                fallback(R.drawable.person_150)
                transformations(RoundedCornersTransformation(20f, 20f, 20f, 20f))
            }
            userName.text = userDataDetails.name ?: getString(R.string.unknown)
            companyName.text =
                getString(R.string.company, userDataDetails.company ?: getString(R.string.unknown))
            location.text = getString(
                R.string.location,
                userDataDetails.location ?: getString(R.string.unknown)
            )
            email.text =
                getString(R.string.email, userDataDetails.email ?: getString(R.string.unknown))
            hireableIcon.isEnabled = userDataDetails.hireable == true
            twitterUsername.text = getString(
                R.string.twitter_username,
                userDataDetails.twitter_username ?: getString(R.string.unknown)
            )
            publicRepos.text = getString(R.string.public_repos, userDataDetails.public_repos ?: 0)
            publicGists.text = getString(R.string.public_gists, userDataDetails.public_gists ?: 0)
            followers.text = getString(R.string.followers, userDataDetails.followers ?: 0)
            following.text = getString(R.string.following, userDataDetails.following ?: 0)
            created.text = getString(
                R.string.created,
                userDataDetails.created_at ?: getString(R.string.unknown)
            )
            updated.text = getString(
                R.string.updated,
                userDataDetails.updated_at ?: getString(R.string.unknown)
            )
            userDataDetails.bio?.let {
                bio.visibility = VISIBLE
                bioTitle.visibility = VISIBLE
                bio.text = it
            }
        }
    }

    private fun manageErrorState(error: String) {
        binding.progressPb.visibility = GONE
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(error)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}