package com.example.spacemission.ui.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spacemission.R
import com.example.spacemission.databinding.FragmentFavoriteListBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoriteListFragment : Fragment() {

    lateinit var favoriteStationBinding: FragmentFavoriteListBinding
    private val favViewModel : FavoriteListViewModel by viewModels()
    private lateinit var favListAdapter: FavListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        favoriteStationBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_favorite_list,container, false)
        return favoriteStationBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFavListLiveData()
        loadFavList()
    }

    fun loadFavList(){
        favListAdapter = FavListAdapter()
        favoriteStationBinding.recyclerFavList.layoutManager = LinearLayoutManager(context)
        favoriteStationBinding.recyclerFavList.setHasFixedSize(true)
        favoriteStationBinding.recyclerFavList.adapter = favListAdapter
        initCardViewButton()
    }

    fun initFavListLiveData(){
        favViewModel.favoriteListLiveData.observe(viewLifecycleOwner, Observer {
            favListAdapter.submitList(it)
        })
    }

    fun initCardViewButton(){
        favListAdapter.setOnFavClickListener {
        val newStation = it
        newStation.isFavorite = false
            favViewModel.updateStation(newStation)
        }
    }

}