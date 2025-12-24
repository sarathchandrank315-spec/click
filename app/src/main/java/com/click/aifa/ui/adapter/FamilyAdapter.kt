package com.click.aifa.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.click.aifa.data.user.FamilyMemberEntity

class FamilyAdapter(
    private val list: MutableList<FamilyMemberEntity>,
    private val onEdit: (FamilyMemberEntity) -> Unit
) : RecyclerView.Adapter<FamilyAdapter.VH>() {

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val name = v.findViewById<TextView>(android.R.id.text1)
        val relation = v.findViewById<TextView>(android.R.id.text2)
    }

    override fun onCreateViewHolder(p: ViewGroup, v: Int): VH {
        val view = LayoutInflater.from(p.context)
            .inflate(android.R.layout.simple_list_item_2, p, false)
        return VH(view)
    }

    override fun onBindViewHolder(h: VH, i: Int) {
        val member = list[i]
        h.name.text = member.name
        h.relation.text = "${member.relation} • ${member.age} yrs"
        h.itemView.setOnClickListener { onEdit(member) }
    }

    override fun getItemCount() = list.size
}
