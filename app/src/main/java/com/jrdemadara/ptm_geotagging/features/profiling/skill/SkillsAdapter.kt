package com.jrdemadara.ptm_geotagging.features.profiling.skill

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jrdemadara.ptm_geotagging.R

class SkillsAdapter(private val skills: List<Skills>) :
    RecyclerView.Adapter<SkillsAdapter.SkillsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkillsViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_skill, parent, false)
        return SkillsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SkillsViewHolder, position: Int) {
        val currentSkill = skills[position]
        holder.skillTextView.text = currentSkill.skill
    }

    override fun getItemCount() = skills.size

    class SkillsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val skillTextView: TextView = itemView.findViewById(R.id.textViewSkill)
    }
}