package com.example.kebabapp.fragments.user

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kebabapp.R
import com.example.kebabapp.utilities.DataXX

class AdapterSuggestionsClass(
    private val dataList: ArrayList<DataXX>,
) : RecyclerView.Adapter<AdapterSuggestionsClass.ViewHolderClass>() {
    class ViewHolderClass(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvSuggest: TextView = itemView.findViewById(R.id.suggestText)
        var tvCreated: TextView = itemView.findViewById(R.id.createdAt)
        var tvStatus: TextView = itemView.findViewById(R.id.suggestionsStatus)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolderClass {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_suggest, parent, false)
        return ViewHolderClass(itemView)
    }

    override fun onBindViewHolder(
        holder: ViewHolderClass,
        position: Int,
    ) {
        val currentItem = dataList[position]
        holder.tvSuggest.text = currentItem.subject
        holder.tvStatus.text = ((currentItem.is_reviewed) != 0).toString()
        holder.tvCreated.text = currentItem.created_at
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}
