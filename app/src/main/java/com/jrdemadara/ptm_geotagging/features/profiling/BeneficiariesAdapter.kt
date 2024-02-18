package com.jrdemadara.ptm_geotagging.features.profiling

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jrdemadara.ptm_geotagging.R

class BeneficiariesAdapter(private val persons: List<Beneficiaries>) :
    RecyclerView.Adapter<BeneficiariesAdapter.BeneficiariesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeneficiariesViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_beneficiary, parent, false)
        return BeneficiariesViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BeneficiariesViewHolder, position: Int) {
        val currentPerson = persons[position]
        holder.precinctTextView.text = currentPerson.precinct
        holder.fullnameTextView.text = currentPerson.fullname
        holder.birthdateTextView.text = currentPerson.birthdate
    }

    override fun getItemCount() = persons.size

    class BeneficiariesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val precinctTextView: TextView = itemView.findViewById(R.id.textViewPrecinct)
        val fullnameTextView: TextView = itemView.findViewById(R.id.textViewFullname)
        val birthdateTextView: TextView = itemView.findViewById(R.id.textViewBirthdate)
    }
}