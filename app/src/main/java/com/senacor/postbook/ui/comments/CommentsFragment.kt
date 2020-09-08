package com.senacor.postbook.ui.comments

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.senacor.postbook.R
import com.senacor.postbook.ui.posts.Post
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.general_views.*

@AndroidEntryPoint
class CommentsFragment: Fragment(R.layout.comments_fragment) {

    private val args: CommentsFragmentArgs by navArgs()
    private val viewModel: CommentsViewModel by viewModels()
    private val adapter = CommentsAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        topAppBar.title = getString(R.string.comments)
        viewModel.setPostId(args.postId)

        swipeRefreshLayout.setOnRefreshListener {
            refreshComments()
        }

        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.addItemDecoration(DividerItemDecoration(requireContext(), (recycler.layoutManager as LinearLayoutManager).orientation))
        recycler.adapter = adapter

        observeUi()

        refreshComments()
    }

    private fun observeUi() {
        viewModel.loading.observe(viewLifecycleOwner) {
            swipeRefreshLayout.isRefreshing = it
        }
        viewModel.post.observe(viewLifecycleOwner) { post ->
            if (adapter.currentList.isNotEmpty() && adapter.currentList[0] is Post) {
                adapter.submitList(
                    listOf(
                        Post(post.id, post.title, post.body, post.favorite),
                        *adapter
                            .currentList
                            .subList(1, adapter.currentList.size)
                            .toTypedArray()
                    )
                )
            }
            else {
                adapter.submitList(
                    adapter.currentList.toMutableList().apply {
                        add(0, Post(post.id, post.title, post.body, post.favorite))
                    }
                )
            }
        }
        viewModel.comments.observe(viewLifecycleOwner) { comments ->
            if (adapter.currentList.isNotEmpty() && adapter.currentList[0] is Post) {
                adapter.submitList(
                    listOf(
                        adapter.currentList[0],
                        *comments
                            .map { Comment(it.id, it.name, it.body) }
                            .toTypedArray()
                    )
                )
            }
            else {
                adapter.submitList(
                    listOf(
                        *comments
                            .map { Comment(it.id, it.name, it.body) }
                            .toTypedArray()
                    )
                )
            }
        }
        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (error.isNullOrEmpty()) {
                errorMessage.setText("")
                errorMessage.isVisible = false
            }
            else {
                errorMessage.setText(error)
                errorMessage.isVisible = true
            }
        }
    }

    private fun refreshComments() {
        viewModel.refreshComments()
    }
}