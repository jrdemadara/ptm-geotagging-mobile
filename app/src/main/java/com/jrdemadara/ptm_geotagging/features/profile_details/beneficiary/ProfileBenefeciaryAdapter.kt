package com.jrdemadara.ptm_geotagging.features.profile_details.beneficiary

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.jrdemadara.ptm_geotagging.R
import com.jrdemadara.ptm_geotagging.data.PowerSearchData

class ProfileBenefeciaryAdapter : RecyclerView.Adapter<ProfileBenefeciaryAdapter.ViewHolder>()  {
    private var mList: ArrayList<DetailsBeneficiaries> = ArrayList()

    fun addItems(items: ArrayList<DetailsBeneficiaries>) {
        this.mList = items
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_beneficiaries, parent, false)
        )

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val textViewBeneficiaryName: TextView = itemView.findViewById(R.id.textViewBeneficiariesName)
        val textViewBeneficiaryPrecinct: TextView = itemView.findViewById(R.id.textViewBeneficiariesPrecinct)
        val textViewBeneficiaryBirthdate: TextView = itemView.findViewById(R.id.textViewBeneficiariesBirthdate)

    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val current = mList[position]
        holder.textViewBeneficiaryName.text = current.fullname
        holder.textViewBeneficiaryPrecinct.text = current.precinct
        holder.textViewBeneficiaryBirthdate.text = current.birthdate
    }

    override fun getItemCount() = mList.size
}
