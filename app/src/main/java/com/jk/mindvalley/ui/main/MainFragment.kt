package com.jk.mindvalley.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import com.jk.mindvalley.R
import com.jk.mindvalley.data.categories.Categories
import com.jk.mindvalley.data.channels.Channels
import com.jk.mindvalley.data.new_episode.Media
import com.jk.mindvalley.data.response.Resource
import com.jk.mindvalley.ui.main.adapters.CategoryAdapter
import com.jk.mindvalley.ui.main.adapters.ChannelAdapter
import com.jk.mindvalley.ui.main.adapters.NewEpisodeAdapter
import com.jk.mindvalley.utils.constants.SpaceConstant
import com.jk.mindvalley.utils.ui.EqualSpaceItemDecoration
import com.jk.mindvalley.utils.ui.UiUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.main_fragment.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : Fragment() {

    private val mainViewModel: MainViewModel by navGraphViewModels(R.id.nav) { defaultViewModelProviderFactory }
    private lateinit var newEpisodeAdapter: NewEpisodeAdapter
    private lateinit var channelAdapter: ChannelAdapter
    private lateinit var categoryAdapter: CategoryAdapter

    @Inject
    lateinit var requestManager: RequestManager


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    private fun setupObserver() {
        mainViewModel.finalDataLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
              is  Resource.Success -> {
                    it.data.let { response ->
                        renderNewEpisode(response.newEpisodeData?.data?.media)
                        renderChannel(response.channelData?.data?.channels)
                        renderCategories(response.categoryData?.data?.categories)
                    }
                    showLoader(false)
                }
               is  Resource.Loading -> {
                    showLoader(true)
                }
                is Resource.Error -> {
                    showLoader(false)
                    Toast.makeText(activity, it.exception.localizedMessage, Toast.LENGTH_LONG).show()
                }
            }
        })
    }


    private fun showLoader(isShowing: Boolean) {
        if (isShowing) {
            main.visibility = View.GONE
            progress_circular.visibility = View.VISIBLE
        } else {
            main.visibility = View.VISIBLE
            progress_circular.visibility = View.GONE

        }
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupObserver()
        mainViewModel.setState(MainViewModel.MainState.GetFinalDataEvent)
    }


    private fun setupUI() {


        new_episode_recyclerView.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        channel_recyclerview.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        category_recyclerview.layoutManager =
            GridLayoutManager(activity, 2)

        newEpisodeAdapter = NewEpisodeAdapter(arrayListOf(), requestManager)
        channelAdapter = ChannelAdapter(arrayListOf(), requestManager)
        categoryAdapter = CategoryAdapter(arrayListOf())

        new_episode_recyclerView.addItemDecoration(UiUtil.itemDecoration(new_episode_recyclerView))
        channel_recyclerview.addItemDecoration(UiUtil.itemDecoration(channel_recyclerview))

        category_recyclerview.addItemDecoration(EqualSpaceItemDecoration(SpaceConstant.spaceHeight))

        new_episode_recyclerView.adapter = newEpisodeAdapter
        channel_recyclerview.adapter = channelAdapter
        category_recyclerview.adapter = categoryAdapter

    }

    private fun renderNewEpisode(medias: List<Media>?) {
        medias?.let {
            newEpisodeAdapter.addData(medias)
            newEpisodeAdapter.notifyDataSetChanged()
        }
    }
    private fun renderChannel(channels: List<Channels>?) {
        channels?.let {
            channelAdapter.addData(channels)
            channelAdapter.notifyDataSetChanged()
        }
    } private fun renderCategories(categories: List<Categories>?) {
        categories?.let {
            categoryAdapter.addData(categories)
            categoryAdapter.notifyDataSetChanged()
        }
    }


}