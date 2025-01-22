package com.jrdemadara.ptm_geotagging.features.profiles

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jrdemadara.ptm_geotagging.R
import com.jrdemadara.ptm_geotagging.data.BeneficiaryV

class BeneficiaryListAdapter(private var items: List<BeneficiaryV>) :
    RecyclerView.Adapter<BeneficiaryListAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewName: TextView = itemView.findViewById(R.id.textViewFullname)
        val textViewBirthdate: TextView = itemView.findViewById(R.id.textViewBirthdate)
        val textViewPrecinct: TextView = itemView.findViewById(R.id.textViewPrecinct)
        // Add other views from your item layout
    }

    fun updateData(newData: List<BeneficiaryV>) {
        items = newData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_beneficiary, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.textViewName.text = item.fullname.uppercase()
        holder.textViewBirthdate.text = item.birthdate.uppercase()
        holder.textViewPrecinct.text = item.precinct.uppercase()
    }

    override fun getItemCount(): Int = items.size
}