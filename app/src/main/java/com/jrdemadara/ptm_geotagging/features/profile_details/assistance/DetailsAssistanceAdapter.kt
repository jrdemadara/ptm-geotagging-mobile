package com.jrdemadara.ptm_geotagging.features.profile_details.assistance

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jrdemadara.ptm_geotagging.R

class DetailsAssistanceAdapter : RecyclerView.Adapter<DetailsAssistanceAdapter.ViewHolder>()  {
    private var mList: ArrayList<DetailsAssistance> = ArrayList()

    fun addItems(items: ArrayList<DetailsAssistance>) {
        this.mList = items
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_details_assistance, parent, false)
        )

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val textViewAssistance: TextView = itemView.findViewById(R.id.textViewDetailsAssistance)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val current = mList[position]
        holder.textViewAssistance.text = current.assistance
    }

    override fun getItemCount() = mList.size
}
