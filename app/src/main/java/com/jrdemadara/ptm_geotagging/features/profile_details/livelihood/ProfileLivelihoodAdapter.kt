package com.jrdemadara.ptm_geotagging.features.profile_details.livelihood

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jrdemadara.ptm_geotagging.R
import com.jrdemadara.ptm_geotagging.features.profile_details.livelihood.DetailsLivelihood

class ProfileLivelihoodAdapter : RecyclerView.Adapter<ProfileLivelihoodAdapter.ViewHolder>()  {
    private var mList: ArrayList<DetailsLivelihood> = ArrayList()

    fun addItems(items: ArrayList<DetailsLivelihood>) {
        this.mList = items
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_details_livelihood, parent, false)
        )

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val textViewDetailsLivelihood: TextView = itemView.findViewById(R.id.textViewDetailsLivelihood)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val current = mList[position]
        holder.textViewDetailsLivelihood.text = current.livelihood
    }

    override fun getItemCount() = mList.size
}
