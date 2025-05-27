package vcmsa.projects.pocketplanpoe

import android.content.Context
import android.os.Bundle
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.NumberFormat
import java.util.*

class GoalActivity : AppCompatActivity() {

    private lateinit var tvMinGoalValue: TextView
    private lateinit var tvMaxGoalValue: TextView
    private lateinit var seekMinGoal: SeekBar
    private lateinit var seekMaxGoal: SeekBar
    private val formatter = NumberFormat.getCurrencyInstance(Locale("en", "ZA"))

    private val PREFS_NAME = "PocketPlanPrefs"
    private val KEY_MIN_GOAL = "min_goal"
    private val KEY_MAX_GOAL = "max_goal"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goal)

        tvMinGoalValue = findViewById(R.id.tvMinGoalValue)
        tvMaxGoalValue = findViewById(R.id.tvMaxGoalValue)
        seekMinGoal = findViewById(R.id.seekMinGoal)
        seekMaxGoal = findViewById(R.id.seekMaxGoal)

        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        // Load saved goals or 0 by default
        val savedMinGoal = prefs.getInt(KEY_MIN_GOAL, 0)
        val savedMaxGoal = prefs.getInt(KEY_MAX_GOAL, 0)

        seekMinGoal.progress = savedMinGoal
        seekMaxGoal.progress = savedMaxGoal

        tvMinGoalValue.text = "Min Goal: ${formatter.format(savedMinGoal)}"
        tvMaxGoalValue.text = "Max Goal: ${formatter.format(savedMaxGoal)}"

        seekMinGoal.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                tvMinGoalValue.text = "Min Goal: ${formatter.format(progress)}"
                saveGoal(KEY_MIN_GOAL, progress)
            }

            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })

        seekMaxGoal.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                tvMaxGoalValue.text = "Max Goal: ${formatter.format(progress)}"
                saveGoal(KEY_MAX_GOAL, progress)
            }

            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })
    }

    private fun saveGoal(key: String, value: Int) {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        with(prefs.edit()) {
            putInt(key, value)
            apply()
        }
    }
}
