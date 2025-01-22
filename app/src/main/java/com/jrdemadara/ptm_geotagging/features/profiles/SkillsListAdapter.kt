package com.jrdemadara.ptm_geotagging.features.profiles

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jrdemadara.ptm_geotagging.R
import com.jrdemadara.ptm_geotagging.data.BeneficiaryV
import com.jrdemadara.ptm_geotagging.data.SkillV

class SkillsListAdapter(private var items: List<SkillV>) :
    RecyclerView.Adapter<SkillsListAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewSkill: TextView = itemView.findViewById(R.id.textViewSkill)
    }

    fun updateData(newData: List<SkillV>) {
        items = newData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_skill, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.textViewSkill.text = item.skill.uppercase()
    }

    override fun getItemCount(): Int = items.size
}