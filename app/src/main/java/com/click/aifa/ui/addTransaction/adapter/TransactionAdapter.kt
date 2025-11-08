package com.click.aifa.ui.addTransaction.adapter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.click.aifa.databinding.ItemTransactionBinding

data class Transaction(
    val title: String,
    val date: String,
    val amount: String,
    val iconRes: Int,
    val isIncome: Boolean
)

class TransactionAdapter(private val list: List<Transaction>) :
    RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    inner class TransactionViewHolder(val binding: ItemTransactionBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val item = list[position]
        holder.binding.tvTitle.text = item.title
        holder.binding.tvTime.text = item.date
        holder.binding.tvAmount.text = item.amount
        holder.binding.tvAmount.setTextColor(
            holder.itemView.context.getColor(
                if (item.isIncome) android.R.color.holo_green_dark else android.R.color.holo_red_dark
            )
        )
    }
}
