package com.jrdemadara.ptm_geotagging.features.profiles

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jrdemadara.ptm_geotagging.R
import com.jrdemadara.ptm_geotagging.data.BeneficiaryV
import com.jrdemadara.ptm_geotagging.data.LivelihoodV
import com.jrdemadara.ptm_geotagging.data.SkillV

class LivelihoodListAdapter(private var items: List<LivelihoodV>) :
    RecyclerView.Adapter<LivelihoodListAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewName: TextView = itemView.findViewById(R.id.textViewLivelihoodName)
        val textViewDetails: TextView = itemView.findViewById(R.id.textViewLivelihoodDetails)
    }

    fun updateData(newData: List<LivelihoodV>) {
        items = newData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_livelihood, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.textViewName.text = item.livelihood.uppercase()
        holder.textViewDetails.text = item.description.uppercase()
    }

    override fun getItemCount(): Int = items.size
}