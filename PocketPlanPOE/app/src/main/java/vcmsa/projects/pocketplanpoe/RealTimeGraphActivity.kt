package vcmsa.projects.pocketplanpoe

import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class RealTimeGraphActivity : AppCompatActivity() {

    private lateinit var barChart: BarChart
    private val uid = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    private val dbRef = FirebaseDatabase.getInstance().getReference("users").child(uid).child("expenses")
    private val goalRef = FirebaseDatabase.getInstance().getReference("users").child(uid).child("goals")

    private var minGoal: Float = 0f
    private var maxGoal: Float = 0f
    private val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_real_time_graph)

        barChart = findViewById(R.id.barChart)

        fetchGoalsAndStartListening()
    }

    private fun fetchGoalsAndStartListening() {
        goalRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                minGoal = snapshot.child("minGoal").getValue(Float::class.java) ?: 0f
                maxGoal = snapshot.child("maxGoal").getValue(Float::class.java) ?: 0f
                listenToExpenseChanges()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun listenToExpenseChanges() {
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val categoryTotals = mutableMapOf<String, Float>()
                for (child in snapshot.children) {
                    val expense = child.getValue(Expense::class.java)
                    if (expense != null) {
                        val category = expense.category ?: "Uncategorized"
                        val amount = expense.amount?.toFloat() ?: 0f
                        categoryTotals[category] = categoryTotals.getOrDefault(category, 0f) + amount
                    }
                }
                drawBarChart(categoryTotals)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun drawBarChart(data: Map<String, Float>) {
        val entries = ArrayList<BarEntry>()
        val labels = ArrayList<String>()
        var index = 0

        for ((category, total) in data) {
            entries.add(BarEntry(index.toFloat(), total))
            labels.add(category)
            index++
        }

        val dataSet = BarDataSet(entries, "Amount Spent Per Category").apply {
            valueTextSize = 14f
        }

        val barData = BarData(dataSet)
        barChart.data = barData
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        barChart.xAxis.granularity = 1f
        barChart.xAxis.setDrawGridLines(false)
        barChart.axisLeft.setDrawGridLines(true)
        barChart.axisRight.isEnabled = false

        // Goal lines
        val minLine = LimitLine(minGoal, "Min Goal").apply { lineColor = android.graphics.Color.GREEN }
        val maxLine = LimitLine(maxGoal, "Max Goal").apply { lineColor = android.graphics.Color.RED }

        barChart.axisLeft.removeAllLimitLines()
        barChart.axisLeft.addLimitLine(minLine)
        barChart.axisLeft.addLimitLine(maxLine)

        barChart.invalidate()
    }
}
