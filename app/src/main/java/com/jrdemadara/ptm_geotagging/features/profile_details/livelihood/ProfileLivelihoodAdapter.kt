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
        val textViewDetailsLivelihoodName: TextView = itemView.findViewById(R.id.textViewDetailsLivelihoodName)
        val textViewDetailsLivelihoodDescription: TextView = itemView.findViewById(R.id.textViewDetailsLivelihoodDescription)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val current = mList[position]
        holder.textViewDetailsLivelihoodName.text = current.livelihood
        holder.textViewDetailsLivelihoodDescription.text = current.description
    }

    override fun getItemCount() = mList.size
}
