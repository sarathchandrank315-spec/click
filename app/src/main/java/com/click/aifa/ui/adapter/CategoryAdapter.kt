package com.click.aifa.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.click.aifa.R
import com.click.aifa.data.Category
import com.google.android.material.card.MaterialCardView

class CategoryAdapter(
    private val categories: MutableList<Category>,
    private val onAddClick: () -> Unit
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val card = view.findViewById<MaterialCardView>(R.id.cardCategory)
        val text = view.findViewById<TextView>(R.id.tvCategory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category_chip, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = categories.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = categories[position]
        holder.text.text = item.name

        // UI STATE
        if (item.isSelected) {
            holder.card.setCardBackgroundColor(Color.parseColor("#7C4DFF"))
            holder.text.setTextColor(Color.WHITE)
            holder.card.strokeWidth = 0
        } else {
            holder.card.setCardBackgroundColor(Color.WHITE)
            holder.text.setTextColor(Color.BLACK)
            holder.card.strokeColor = Color.GRAY
        }

        holder.card.setOnClickListener {
            if (item.name == "+") {
                onAddClick()
                return@setOnClickListener
            }

            // SINGLE SELECTION
            categories.forEach { it.isSelected = false }
            item.isSelected = true
            notifyDataSetChanged()
        }
    }
}
