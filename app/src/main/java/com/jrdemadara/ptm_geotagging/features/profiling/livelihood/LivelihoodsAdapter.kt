package com.jrdemadara.ptm_geotagging.features.profiling.livelihood

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jrdemadara.ptm_geotagging.R

class LivelihoodsAdapter(private val livelihood: List<Livelihood>) :
    RecyclerView.Adapter<LivelihoodsAdapter.LivelihoodViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LivelihoodViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_livelihoodl, parent, false)
        return LivelihoodViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: LivelihoodViewHolder, position: Int) {
        val current = livelihood[position]
        holder.livelihoodTextView.text = current.livelihood
    }

    override fun getItemCount() = livelihood.size

    class LivelihoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val livelihoodTextView: TextView = itemView.findViewById(R.id.textViewLivelihood)
    }
}