package com.senacor.postbook.ui.posts

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.senacor.postbook.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.posts_fragment.*

@AndroidEntryPoint
class PostsFragment: Fragment(R.layout.posts_fragment) {

    private val args: PostsFragmentArgs by navArgs()
    private val viewModel: PostsViewModel by viewModels()
    private val adapter = PostsAdapter()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipeRefreshLayout.setOnRefreshListener {
            refreshPosts()
        }

        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter

        observeUi()

        refreshPosts()
    }

    private fun observeUi() {
        viewModel.loading.observe(viewLifecycleOwner) {
            swipeRefreshLayout.isRefreshing = it
        }
        viewModel.posts.observe(viewLifecycleOwner) {
            println("new data")
            adapter.submitList(it.map { Post(it.id, it.title, it.body, it.favorite) })
        }
        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (error.isNullOrEmpty()) {
                errorMessage.setText("")
                errorMessage.isVisible = false
            } else {
                errorMessage.setText(error)
                errorMessage.isVisible = true
            }
        }
    }

    private fun refreshPosts() {
        viewModel.refreshPosts(args.userId)
    }
}