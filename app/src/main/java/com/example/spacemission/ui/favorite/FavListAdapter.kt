package com.example.spacemission.ui.favorite

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.spacemission.R
import com.example.spacemission.database.entities.SpaceStation
import com.example.spacemission.databinding.CardFavoriteStationBinding
import kotlin.math.pow
import kotlin.math.sqrt

class FavListAdapter() : RecyclerView.Adapter<FavListAdapter.FavListViewHolder>() {

    private val favList: MutableList<SpaceStation> = mutableListOf()
    private lateinit var favCardBinding: CardFavoriteStationBinding
    private var onFavClickListener: ((SpaceStation) -> Unit)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FavListAdapter.FavListViewHolder {
        favCardBinding =
            CardFavoriteStationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavListViewHolder(favCardBinding)
    }

    override fun onBindViewHolder(holder: FavListAdapter.FavListViewHolder, position: Int) {
        holder.bind(favList[position])
    }

    override fun getItemCount(): Int {
        return favList.size
    }

    inner class FavListViewHolder(private val binding: CardFavoriteStationBinding) :
        RecyclerView.ViewHolder(favCardBinding.root) {
        fun bind(stationItem: SpaceStation) {
            val distance =
                sqrt((stationItem.coordinateX).pow(2) + (stationItem.coordinateY).pow(2)).toInt()

            binding.txtPlanetName.text = stationItem.name
            binding.txtAmountOfTime.text =
                binding.root.context.resources.getString(R.string.amout_of_EUS, distance)
            binding.txtCapacity.text = stationItem.capacity.toString()
            binding.imgFavoriteIcon.setOnClickListener {
                onFavClickListener?.invoke(stationItem)
                removeItem(favList.indexOf(stationItem))

            }
        }
    }

    fun setOnFavClickListener(onItemClickListener: ((SpaceStation) -> Unit)?) {
        this.onFavClickListener = onItemClickListener
    }

    fun submitList(newList: List<SpaceStation>) {
        favList.clear()
        favList.addAll(newList)
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        favList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, favList.size)

    }


}