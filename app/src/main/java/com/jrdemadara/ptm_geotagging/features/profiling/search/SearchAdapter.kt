package com.jrdemadara.ptm_geotagging.features.profiling.search

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.jrdemadara.ptm_geotagging.R
import com.jrdemadara.ptm_geotagging.data.Members
import com.jrdemadara.ptm_geotagging.data.SearchMembers

class SearchAdapter : RecyclerView.Adapter<SearchAdapter.ViewHolder>()  {
    private var mList: ArrayList<SearchMembers> = ArrayList()
    private var onClickItem: ((SearchMembers) -> Unit)? = null

    fun addItems(items: ArrayList<SearchMembers>){
        this.mList = items
        notifyDataSetChanged()
    }

    fun setOnClickItem(callback: (SearchMembers) -> Unit) {
        this.onClickItem = callback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_search_member, parent, false)
        )

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val memberTextView: TextView = itemView.findViewById(R.id.textViewMemberName)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = mList[position]
        holder.itemView.setOnClickListener {
            onClickItem?.invoke(current)
        }

        holder.memberTextView.text = buildString {
            append(current.lastname.replaceFirstChar (Char::uppercase))
            append(", ")
            append(current.firstname.replaceFirstChar (Char::uppercase))
            append(" ")
            append(current.middlename.replaceFirstChar (Char::uppercase))
            append(" ")
            append(current.extension.replaceFirstChar (Char::uppercase))
        }
    }

    override fun getItemCount() = mList.size
}