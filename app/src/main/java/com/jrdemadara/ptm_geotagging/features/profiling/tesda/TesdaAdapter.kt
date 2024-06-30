package com.jrdemadara.ptm_geotagging.features.profiling.tesda

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jrdemadara.ptm_geotagging.R

class TesdaAdapter(private val tesda: List<Tesda>) :
    RecyclerView.Adapter<TesdaAdapter.TesdaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TesdaViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tesda, parent, false)
        return TesdaViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TesdaViewHolder, position: Int) {
        val current = tesda[position]
        holder.textViewTesdaName.text = current.name
        holder.textViewTesdaCourse.text = current.course
    }

    override fun getItemCount() = tesda.size

    class TesdaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewTesdaName: TextView = itemView.findViewById(R.id.textViewTesdaName)
        val textViewTesdaCourse: TextView = itemView.findViewById(R.id.textViewTesdaCourse)
    }
}