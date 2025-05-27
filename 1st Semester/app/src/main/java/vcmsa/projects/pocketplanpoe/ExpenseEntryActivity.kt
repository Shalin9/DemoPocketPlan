package vcmsa.projects.pocketplanpoe

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class ExpenseEntryActivity : AppCompatActivity() {

    private lateinit var dateInput: EditText
    private lateinit var startInput: EditText
    private lateinit var endInput: EditText
    private lateinit var descriptionInput: EditText
    private lateinit var categorySpinner: Spinner
    private lateinit var amountInput: EditText
    private lateinit var btnAddPhoto: Button
    private lateinit var imgPhoto: ImageView
    private lateinit var saveButton: Button

    private var selectedCategory: String = ""
    private var imageUri: Uri? = null
    private val IMAGE_PICK_CODE = 1000
    private val uid = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense_entry)

        dateInput = findViewById(R.id.dateInput)
        startInput = findViewById(R.id.startInput)
        endInput = findViewById(R.id.endInput)
        descriptionInput = findViewById(R.id.descriptionInput)
        categorySpinner = findViewById(R.id.categoryInput)
        amountInput = findViewById(R.id.amountInput)
        btnAddPhoto = findViewById(R.id.btnAddPhoto)
        imgPhoto = findViewById(R.id.imgPhoto)
        saveButton = findViewById(R.id.saveButton)

        val calendar = Calendar.getInstance()

        // Date picker
        dateInput.setOnClickListener {
            DatePickerDialog(this, { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                dateInput.setText(sdf.format(calendar.time))
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        // Time pickers
        fun showTimePicker(editText: EditText) {
            TimePickerDialog(this, { _, hourOfDay, minute ->
                val time = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)
                editText.setText(time)
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
        }

        startInput.setOnClickListener { showTimePicker(startInput) }
        endInput.setOnClickListener { showTimePicker(endInput) }

        // Category Spinner setup
        val categories = listOf("Select Category", "Food", "Transport", "Utilities", "Entertainment", "Health", "Other")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = spinnerAdapter

        categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                selectedCategory = categories[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                selectedCategory = ""
            }
        }

        btnAddPhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK).apply {
                type = "image/*"
            }
            startActivityForResult(intent, IMAGE_PICK_CODE)
        }

        saveButton.setOnClickListener {
            if (selectedCategory == "Select Category" || selectedCategory.isEmpty()) {
                Toast.makeText(this, "Please select a valid category", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val expense = Expense(
                date = dateInput.text.toString(),
                startTime = startInput.text.toString(),
                endTime = endInput.text.toString(),
                description = descriptionInput.text.toString(),
                category = selectedCategory,
                amount = amountInput.text.toString().toDoubleOrNull() ?: 0.0,
                photoUri = imageUri?.toString() ?: ""
            )

            FirebaseDatabase.getInstance().getReference("users")
                .child(uid)
                .child("expenses")
                .push()
                .setValue(expense)
                .addOnSuccessListener {
                    Toast.makeText(this, "Expense saved", Toast.LENGTH_SHORT).show()
                    finish()
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK) {
            imageUri = data?.data
            imgPhoto.setImageURI(imageUri)
        }
    }
}
