package com.carlocator.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.carlocator.databinding.PlaceListItemBinding
import com.carlocator.model.Placemarks

/**
 * @author ashish <ashish.bhandari>
 */
class LocatorAdapter : RecyclerView.Adapter<LocatorAdapter.LocatorHolder>() {

    private var placeMarkers: List<Placemarks> = emptyList()


    override fun onBindViewHolder(holder: LocatorHolder, position: Int) {

        holder.bind(placeMarkers[position])

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocatorHolder{
        return LocatorHolder(PlaceListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return placeMarkers.size
    }

    fun updateItems(items: List<Placemarks>) {
        placeMarkers = items
        notifyDataSetChanged()
    }


    class LocatorHolder(
            private val binding: PlaceListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Placemarks) {
            binding.apply {
                binding.placemarks = item
                executePendingBindings()
            }
        }
    }

}