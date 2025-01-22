package com.jrdemadara.ptm_geotagging.features.profiles

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jrdemadara.ptm_geotagging.R
import com.jrdemadara.ptm_geotagging.data.AssistanceV
import com.jrdemadara.ptm_geotagging.data.BeneficiaryV

class AssistanceListAdapter(private var items: List<AssistanceV>) :
    RecyclerView.Adapter<AssistanceListAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewVLAssistance: TextView = itemView.findViewById(R.id.textViewVLAssistance)
        val textViewVLDate: TextView = itemView.findViewById(R.id.textViewVLDate)
        val textViewVLAmount: TextView = itemView.findViewById(R.id.textViewVLAmount)
        // Add other views from your item layout
    }

    fun updateData(newData: List<AssistanceV>) {
        items = newData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_vlassistance, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.textViewVLAssistance.text = item.assistance.uppercase()
        holder.textViewVLDate.text = item.amount.toString()
        holder.textViewVLAmount.text = item.released_at
    }

    override fun getItemCount(): Int = items.size
}