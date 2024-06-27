package com.jrdemadara.ptm_geotagging.features.profile_details.skills

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jrdemadara.ptm_geotagging.R

class ProfileSkillsAdapter : RecyclerView.Adapter<ProfileSkillsAdapter.ViewHolder>()  {
    private var mList: ArrayList<DetailsSkills> = ArrayList()

    fun addItems(items: ArrayList<DetailsSkills>) {
        this.mList = items
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_details_skill, parent, false)
        )

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val textViewSkill: TextView = itemView.findViewById(R.id.textViewDetailsSkill)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val current = mList[position]
        holder.textViewSkill.text = current.skills
    }

    override fun getItemCount() = mList.size
}
