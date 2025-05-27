package vcmsa.projects.pocketplanpoe

data class Expense(
    val date: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val description: String = "",
    val category: String = "",
    val amount: Double = 0.0,
    val photoUri: String? = null  // optional
)
