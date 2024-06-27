package com.jrdemadara.ptm_geotagging.features.search

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

class PowerSearchAdapter : RecyclerView.Adapter<PowerSearchAdapter.ViewHolder>()  {
    private var mList: ArrayList<PowerSearchData> = ArrayList()
    private var onClickItem: ((PowerSearchData) -> Unit)? = null
    private var selectedPosition = -1 // Add this variable

    fun addItems(items: ArrayList<PowerSearchData>) {
        this.mList = items
        notifyDataSetChanged()
    }

    fun setOnClickItem(callback: (PowerSearchData) -> Unit) {
        this.onClickItem = callback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_power_search, parent, false)
        )

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val textViewProfileName: TextView = itemView.findViewById(R.id.textViewProfileName)
        val textViewIsUploaded: TextView = itemView.findViewById(R.id.textViewIsUploaded)
        val itemCardView: View = itemView.findViewById(R.id.cardviewPowerSearch) // Assuming CardView ID
    }

    private fun setTextViewColor(context: Context, textView: TextView, isUploaded: Int) {
        val colorResId = if (isUploaded == 1) R.color.green else R.color.red
        textView.setTextColor(ContextCompat.getColor(context, colorResId))
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val current = mList[position]
        holder.itemView.setOnClickListener {
            onClickItem?.invoke(current)
            // Update the selected position and notify the adapter
            selectedPosition = position
            notifyDataSetChanged()
        }

        // Set the profile name
        holder.textViewProfileName.text = buildString {
            append(current.lastname.replaceFirstChar (Char::uppercase))
            append(", ")
            append(current.firstname.replaceFirstChar (Char::uppercase))
            append(" ")
            append(current.middlename.replaceFirstChar (Char::uppercase))
            append(" ")
            append(current.extension.replaceFirstChar (Char::uppercase))
        }

        // Set the upload status text and color
        holder.textViewIsUploaded.apply {
            text = if (current.isUploaded == 1) "Uploaded" else "Not Uploaded"
            setTextViewColor(context, this, current.isUploaded)
        }

        // Change the background color if the item is selected
        holder.itemCardView.apply {
            val backgroundColorResId = if (position == selectedPosition) {
                R.color.selectedCardviewBackground // Change this to your desired selected color
            } else {
                R.color.defaultCardviewBackground // Change this to your default color
            }
            setBackgroundColor(ContextCompat.getColor(context, backgroundColorResId))
        }
    }

    override fun getItemCount() = mList.size
}
