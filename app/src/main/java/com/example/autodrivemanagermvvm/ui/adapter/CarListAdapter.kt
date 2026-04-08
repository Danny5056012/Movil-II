package com.example.autodrivemanagermvvm.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.autodrivemanagermvvm.R
import com.example.autodrivemanagermvvm.data.model.CarUiModel
import com.example.autodrivemanagermvvm.databinding.ItemCarBinding

class CarListAdapter(
    private val onItemClick: (CarUiModel) -> Unit
) : ListAdapter<CarUiModel, CarListAdapter.Vh>(Diff) {

    object Diff : DiffUtil.ItemCallback<CarUiModel>() {
        override fun areItemsTheSame(oldItem: CarUiModel, newItem: CarUiModel): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: CarUiModel, newItem: CarUiModel): Boolean =
            oldItem == newItem
    }

    inner class Vh(private val binding: ItemCarBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CarUiModel) {
            binding.ivCar.setImageResource(item.imageResId)
            binding.tvTitle.text = "${item.make} ${item.model} (${item.year})"
            binding.tvSpeed.text = "Velocidad: ${item.speed} km/h"

            val lowFuel = item.fuel < 15
            binding.ivFuelWarning.visibility = if (lowFuel) android.view.View.VISIBLE else android.view.View.GONE
            binding.tvFuel.text = "Combustible: ${item.fuel}"
            binding.tvFuel.setTextColor(
                if (lowFuel) ContextCompat.getColor(binding.root.context, R.color.danger_red)
                else ContextCompat.getColor(binding.root.context, R.color.brand_on_surface)
            )

            binding.root.setOnClickListener { onItemClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        val binding = ItemCarBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Vh(binding)
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.bind(getItem(position))
    }
}

