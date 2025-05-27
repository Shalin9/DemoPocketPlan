package vcmsa.projects.pocketplanpoe

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class AddCategoryActivity : AppCompatActivity() {

    private lateinit var etCategoryName: EditText
    private lateinit var btnSaveCategory: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_category)

        etCategoryName = findViewById(R.id.etCategoryName)
        btnSaveCategory = findViewById(R.id.btnSaveCategory)

        btnSaveCategory.setOnClickListener {
            val name = etCategoryName.text.toString().trim()
            if (name.isEmpty()) {
                Toast.makeText(this, "Category name cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userId = FirebaseAuth.getInstance().currentUser?.uid
            val dbRef = FirebaseDatabase.getInstance().getReference("Categories").child(userId!!)

            val categoryId = UUID.randomUUID().toString()
            val category = Category(categoryId, name)

            dbRef.child(categoryId).setValue(category).addOnSuccessListener {
                Toast.makeText(this, "Category saved!", Toast.LENGTH_SHORT).show()
                finish()
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to save category", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
