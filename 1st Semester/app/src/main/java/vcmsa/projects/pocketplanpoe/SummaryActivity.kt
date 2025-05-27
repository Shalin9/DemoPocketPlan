package vcmsa.projects.pocketplanpoe

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class SummaryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ExpenseAdapter
    private lateinit var dateButton: Button
    private lateinit var selectedDateText: TextView

    private val expenseList = mutableListOf<Expense>()
    private val uid = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    private val dbRef = FirebaseDatabase.getInstance().getReference("users").child(uid).child("expenses")
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_summary)

        recyclerView = findViewById(R.id.recyclerExpenses)
        dateButton = findViewById(R.id.btnSelectDate)
        selectedDateText = findViewById(R.id.tvSelectedDate)

        adapter = ExpenseAdapter(this, expenseList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        dateButton.setOnClickListener { showDatePicker() }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(this, { _, year, month, day ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, month, day)
            val formattedDate = dateFormat.format(selectedDate.time)
            selectedDateText.text = "Selected Date: $formattedDate"
            loadExpensesForDate(formattedDate)
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun loadExpensesForDate(date: String) {
        dbRef.orderByChild("date").equalTo(date).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val newList = mutableListOf<Expense>()
                for (child in snapshot.children) {
                    val expense = child.getValue(Expense::class.java)
                    if (expense != null) {
                        newList.add(expense)
                    }
                }
                adapter.updateList(newList)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SummaryActivity, "Failed to load expenses.", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
