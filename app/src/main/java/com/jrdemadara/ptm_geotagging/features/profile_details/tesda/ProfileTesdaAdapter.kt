package com.jrdemadara.ptm_geotagging.features.profile_details.tesda

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jrdemadara.ptm_geotagging.R
import com.jrdemadara.ptm_geotagging.features.profile_details.livelihood.DetailsLivelihood

class ProfileTesdaAdapter : RecyclerView.Adapter<ProfileTesdaAdapter.ViewHolder>()  {
    private var mList: ArrayList<DetailsTesda> = ArrayList()

    fun addItems(items: ArrayList<DetailsTesda>) {
        this.mList = items
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_details_tesda, parent, false)
        )

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val textViewDetailsTesda: TextView = itemView.findViewById(R.id.textViewDetailsTesda)
        val textViewDetailsCourse: TextView = itemView.findViewById(R.id.textViewDetailsCourse)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val current = mList[position]
        holder.textViewDetailsTesda.text = current.name
        holder.textViewDetailsCourse.text = current.course
    }

    override fun getItemCount() = mList.size
}
