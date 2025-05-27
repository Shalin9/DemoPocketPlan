package vcmsa.projects.pocketplanpoe

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class GoalsActivity : AppCompatActivity() {

    private lateinit var minGoalInput: EditText
    private lateinit var maxGoalInput: EditText
    private lateinit var btnSaveGoals: Button

    private val uid = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    private val goalsRef = FirebaseDatabase.getInstance().getReference("users").child(uid).child("goals")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goals)

        minGoalInput = findViewById(R.id.minGoalInput)
        maxGoalInput = findViewById(R.id.maxGoalInput)
        btnSaveGoals = findViewById(R.id.btnSaveGoals)

        // Load existing goals if present
        goalsRef.get().addOnSuccessListener { snapshot ->
            val minGoal = snapshot.child("minGoal").getValue(Float::class.java)
            val maxGoal = snapshot.child("maxGoal").getValue(Float::class.java)
            minGoalInput.setText(minGoal?.toString() ?: "")
            maxGoalInput.setText(maxGoal?.toString() ?: "")
        }

        btnSaveGoals.setOnClickListener {
            val minGoalStr = minGoalInput.text.toString()
            val maxGoalStr = maxGoalInput.text.toString()

            val minGoal = minGoalStr.toFloatOrNull()
            val maxGoal = maxGoalStr.toFloatOrNull()

            if (minGoal == null || maxGoal == null) {
                Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val updates = mapOf(
                "minGoal" to minGoal,
                "maxGoal" to maxGoal
            )

            goalsRef.updateChildren(updates).addOnSuccessListener {
                Toast.makeText(this, "Goals saved successfully", Toast.LENGTH_SHORT).show()
                finish()
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to save goals", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
