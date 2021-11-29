package com.optiscan.demo.barcode.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.optiscan.demo.barcode.databinding.ScanningItemsBinding
import com.optiscan.demo.barcode.model.ScanTypes

/**
 * Created by SARATH on 12-07-2021
 */
class ScanningTypesAdapter(private val scanTypesList: List<ScanTypes>, val context: Context, private val onItemClicked: (ScanTypes) -> Unit) : RecyclerView.Adapter<ScanningTypesAdapter.ScanningTypesViewHolder>() {


    inner class ScanningTypesViewHolder(val binding: ScanningItemsBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScanningTypesViewHolder {
        return ScanningTypesViewHolder(ScanningItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ScanningTypesViewHolder, position: Int) {
        val scanType = scanTypesList[position]
        holder.binding.apply {
            tvScanTypeText.text = scanType.type
            ivScanTypeImage.setImageResource(scanType.image)
        }
        holder.binding.rvItem.setOnClickListener {
            onItemClicked(scanType)
        }
    }

    override fun getItemCount(): Int {
        return scanTypesList.size
    }
}