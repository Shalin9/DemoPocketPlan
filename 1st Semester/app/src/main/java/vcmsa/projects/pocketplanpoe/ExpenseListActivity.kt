package vcmsa.projects.pocketplanpoe

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class ExpenseListActivity : AppCompatActivity() {

    private lateinit var etStartDate: EditText
    private lateinit var etEndDate: EditText
    private lateinit var btnFilter: Button
    private lateinit var rvExpenses: RecyclerView
    private lateinit var adapter: ExpenseAdapter

    private val expenses = mutableListOf<Expense>()
    private val uid = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense_list)

        etStartDate = findViewById(R.id.etStartDate)
        etEndDate = findViewById(R.id.etEndDate)
        btnFilter = findViewById(R.id.btnFilter)
        rvExpenses = findViewById(R.id.rvExpenses)

        rvExpenses.layoutManager = LinearLayoutManager(this)
        adapter = ExpenseAdapter(this, expenses)
        rvExpenses.adapter = adapter

        etStartDate.setOnClickListener { showDatePicker(etStartDate) }
        etEndDate.setOnClickListener { showDatePicker(etEndDate) }

        btnFilter.setOnClickListener {
            val startDateStr = etStartDate.text.toString()
            val endDateStr = etEndDate.text.toString()

            if (startDateStr.isEmpty() || endDateStr.isEmpty()) {
                Toast.makeText(this, "Please select both start and end dates", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val startDate = dateFormat.parse(startDateStr)
            val endDate = dateFormat.parse(endDateStr)

            if (startDate == null || endDate == null) {
                Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (startDate.after(endDate)) {
                Toast.makeText(this, "Start date must be before end date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            loadExpenses(startDate, endDate)
        }
    }

    private fun showDatePicker(targetEditText: EditText) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(this,
            { _, year, month, dayOfMonth ->
                val dateStr = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                targetEditText.setText(dateStr)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun loadExpenses(startDate: Date, endDate: Date) {
        val ref = FirebaseDatabase.getInstance().getReference("users").child(uid).child("expenses")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                expenses.clear()

                for (expenseSnap in snapshot.children) {
                    val expense = expenseSnap.getValue(Expense::class.java)
                    if (expense != null) {
                        val expenseDate = dateFormat.parse(expense.date)
                        if (expenseDate != null && !expenseDate.before(startDate) && !expenseDate.after(endDate)) {
                            expenses.add(expense)
                        }
                    }
                }
                adapter.updateList(expenses)
                if (expenses.isEmpty()) {
                    Toast.makeText(this@ExpenseListActivity, "No expenses found in this period", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ExpenseListActivity, "Failed to load expenses: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
