package vcmsa.projects.pocketplanpoe

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ExpenseAdapter(
    private val context: Context,
    private var expenses: List<Expense>
) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    inner class ExpenseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgPhoto: ImageView = view.findViewById(R.id.imgPhoto)
        val tvDescription: TextView = view.findViewById(R.id.tvDescription)
        val tvDateAmount: TextView = view.findViewById(R.id.tvDateAmount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_expense, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenses[position]

        holder.tvDescription.text = expense.description
        holder.tvDateAmount.text = "${expense.date} - R${"%.2f".format(expense.amount)}"

        if (!expense.photoUri.isNullOrEmpty()) {
            holder.imgPhoto.visibility = View.VISIBLE
            Glide.with(context).load(expense.photoUri).into(holder.imgPhoto)

            holder.imgPhoto.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(expense.photoUri))
                context.startActivity(intent)
            }
        } else {
            holder.imgPhoto.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = expenses.size

    fun updateList(newList: List<Expense>) {
        expenses = newList
        notifyDataSetChanged()
    }
}
