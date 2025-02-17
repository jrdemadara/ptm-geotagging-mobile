package com.jrdemadara.ptm_geotagging.features.assistance_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jrdemadara.ptm_geotagging.R
import com.jrdemadara.ptm_geotagging.features.profile_details.livelihood.DetailsLivelihood

class AssistanceListAdapter(private var list: List<AssistanceList>) :
    RecyclerView.Adapter<AssistanceListAdapter.AssistanceListViewHolder>() {

    fun addItems(items: List<AssistanceList>?) {
        if (items != null) {
            this.list = items
        }
        notifyDataSetChanged()
    }

    fun clear() {
        this.list = emptyList()  // You can also use list.clear() if it's a mutable list
        notifyDataSetChanged()  // Notify the adapter that the data has been cleared
    }

    // Function to get the sum of all amounts
    fun getTotalAmount(): Double {
        var totalAmount = 0.0
        for (assistance in list) {
            // Assuming `amount` is a String, and converting it to Double
            totalAmount += assistance.amount.toDoubleOrNull() ?: 0.0
        }
        return totalAmount
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssistanceListViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_assistance_list, parent, false)
        return AssistanceListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AssistanceListViewHolder, position: Int) {
        val current = list[position]
        holder.textViewName.text = current.fullname
        holder.textViewAddress.text = current.purok + ", " + current.barangay
        holder.textViewEndorser.text = current.endorser
        holder.textViewAssistance.text = current.assistance
        holder.textViewDate.text = current.date
        holder.textViewAmount.text = current.amount
    }

    override fun getItemCount() = list.size



    class AssistanceListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewName: TextView = itemView.findViewById(R.id.textViewALName)
        val textViewAddress: TextView = itemView.findViewById(R.id.textViewALAddress)
        val textViewEndorser: TextView = itemView.findViewById(R.id.textViewALEndorser)
        val textViewAmount: TextView = itemView.findViewById(R.id.textViewALAmount)
        val textViewAssistance: TextView = itemView.findViewById(R.id.textViewALAssistance)
        val textViewDate: TextView = itemView.findViewById(R.id.textViewALDate)
    }
}