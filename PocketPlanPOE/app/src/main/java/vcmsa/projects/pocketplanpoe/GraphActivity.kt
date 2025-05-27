package vcmsa.projects.pocketplanpoe

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class GraphActivity : AppCompatActivity() {

    private lateinit var barChart: BarChart
    private val uid = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    private val dbRef = FirebaseDatabase.getInstance().getReference("users").child(uid)
    private val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    private var minGoal: Float = 500f
    private var maxGoal: Float = 3000f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph)
        barChart = findViewById(R.id.barChart)

        fetchGoalsAndLoadGraph()
    }

    private fun fetchGoalsAndLoadGraph() {
        dbRef.child("goals").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                minGoal = snapshot.child("minGoal").getValue(Float::class.java) ?: 500f
                maxGoal = snapshot.child("maxGoal").getValue(Float::class.java) ?: 3000f
                loadGraph()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@GraphActivity, "Failed to load goals", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadGraph() {
        val expensesRef = dbRef.child("expenses")
        val endDate = Calendar.getInstance().time
        val startDate = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -30) }.time

        expensesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val categoryTotals = mutableMapOf<String, Float>()

                for (child in snapshot.children) {
                    val expense = child.getValue(Expense::class.java)

                    if (expense != null && !expense.date.isNullOrEmpty()) {
                        val expenseDate = formatter.parse(expense.date)
                        if (expenseDate != null && expenseDate in startDate..endDate) {
                            val category = expense.category ?: "Uncategorized"
                            val amount = expense.amount?.toFloat() ?: 0f  // ✅ Fix is here
                            categoryTotals[category] = categoryTotals.getOrDefault(category, 0f) + amount
                        }
                    }
                }


                drawColoredBarChart(categoryTotals)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@GraphActivity, "Failed to load expenses: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun drawColoredBarChart(data: Map<String, Float>) {
        val entries = ArrayList<BarEntry>()
        val labels = ArrayList<String>()
        val colors = ArrayList<Int>()
        var index = 0

        for ((category, total) in data) {
            entries.add(BarEntry(index.toFloat(), total))
            labels.add(category)

            val color = when {
                total < minGoal -> Color.YELLOW
                total > maxGoal -> Color.RED
                else -> Color.GREEN
            }
            colors.add(color)

            index++
        }

        val dataSet = BarDataSet(entries, "Spending per Category").apply {
            valueTextSize = 14f
            setColors(colors)
        }

        barChart.data = BarData(dataSet)
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        barChart.xAxis.granularity = 1f
        barChart.xAxis.setDrawGridLines(false)
        barChart.axisLeft.setDrawGridLines(true)
        barChart.axisRight.isEnabled = false

        // Add goal lines
        val minLine = LimitLine(minGoal, "Min Goal").apply { lineColor = Color.YELLOW }
        val maxLine = LimitLine(maxGoal, "Max Goal").apply { lineColor = Color.RED }

        barChart.axisLeft.removeAllLimitLines()
        barChart.axisLeft.addLimitLine(minLine)
        barChart.axisLeft.addLimitLine(maxLine)

        barChart.description.isEnabled = false
        barChart.invalidate()
    }
}
