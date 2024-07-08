package com.jrdemadara.ptm_geotagging.features.profiling.assistance

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jrdemadara.ptm_geotagging.R

class AssistanceAdapter(private val assistance: List<Assistance>) :
    RecyclerView.Adapter<AssistanceAdapter.AssistanceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssistanceViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_assistance, parent, false)
        return AssistanceViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AssistanceViewHolder, position: Int) {
        val current = assistance[position]
        holder.textViewName.text = current.assistance
    }

    override fun getItemCount() = assistance.size

    class AssistanceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewName: TextView = itemView.findViewById(R.id.textViewAssistanceName)
    }
}