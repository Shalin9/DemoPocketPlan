package vcmsa.projects.pocketplanpoe

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class CategorySummaryActivity : AppCompatActivity() {

    private lateinit var startDateInput: EditText
    private lateinit var endDateInput: EditText
    private lateinit var btnLoadSummary: Button
    private lateinit var listView: ListView

    private val uid = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_summary)

        startDateInput = findViewById(R.id.startDateInput)
        endDateInput = findViewById(R.id.endDateInput)
        btnLoadSummary = findViewById(R.id.btnLoadSummary)
        listView = findViewById(R.id.listView)

        val calendar = Calendar.getInstance()

        // Date pickers
        startDateInput.setOnClickListener {
            showDatePicker(startDateInput, calendar)
        }
        endDateInput.setOnClickListener {
            showDatePicker(endDateInput, calendar)
        }

        btnLoadSummary.setOnClickListener {
            val startDateStr = startDateInput.text.toString()
            val endDateStr = endDateInput.text.toString()

            if (startDateStr.isEmpty() || endDateStr.isEmpty()) {
                Toast.makeText(this, "Please select both start and end dates", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            loadCategorySummary(startDateStr, endDateStr)
        }
    }

    private fun showDatePicker(editText: EditText, calendar: Calendar) {
        DatePickerDialog(this, { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            editText.setText(dateFormat.format(calendar.time))
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun loadCategorySummary(startDateStr: String, endDateStr: String) {
        val expenseRef = FirebaseDatabase.getInstance()
            .getReference("users")
            .child(uid)
            .child("expenses")

        expenseRef.get().addOnSuccessListener { snapshot ->
            val summaryMap = mutableMapOf<String, Double>()

            for (expenseSnapshot in snapshot.children) {
                val expense = expenseSnapshot.getValue(Expense::class.java) ?: continue

                // Check if expense date is within the selected range
                val expenseDate = try {
                    dateFormat.parse(expense.date)
                } catch (e: Exception) {
                    null
                }

                val startDate = try {
                    dateFormat.parse(startDateStr)
                } catch (e: Exception) {
                    null
                }

                val endDate = try {
                    dateFormat.parse(endDateStr)
                } catch (e: Exception) {
                    null
                }

                if (expenseDate != null && startDate != null && endDate != null) {
                    if (!expenseDate.before(startDate) && !expenseDate.after(endDate)) {
                        // Aggregate amount by category
                        val currentTotal = summaryMap[expense.category] ?: 0.0
                        summaryMap[expense.category] = currentTotal + expense.amount
                    }
                }
            }

            if (summaryMap.isEmpty()) {
                Toast.makeText(this, "No expenses found in the selected period", Toast.LENGTH_SHORT).show()
                listView.adapter = null
            } else {
                val listItems = summaryMap.map { (category, total) ->
                    "$category: R${"%.2f".format(total)}"
                }
                val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listItems)
                listView.adapter = adapter
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to load expenses", Toast.LENGTH_SHORT).show()
        }
    }
}
