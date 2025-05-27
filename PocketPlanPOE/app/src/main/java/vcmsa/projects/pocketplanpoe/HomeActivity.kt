package vcmsa.projects.pocketplanpoe

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import androidx.appcompat.widget.Toolbar

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Find views manually (replace synthetic usage)
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
        toolbar = findViewById(R.id.toolbar)

        // Setup toolbar
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navigationView.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                // Handle home navigation
            }
            R.id.nav_add_expense -> {
                val intent = Intent(this, ExpenseEntryActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_summary -> {
                val intent = Intent(this, SummaryActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_add_category -> {
                val intent = Intent(this, AddCategoryActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_goal -> {
                val intent = Intent(this, GoalActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_category_summary -> {
                val intent = Intent(this, CategorySummaryActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_spending_graph -> {
                startActivity(Intent(this, RealTimeGraphActivity::class.java))
            }
            R.id.nav_goals -> {
                startActivity(Intent(this, GoalsActivity::class.java))
            }
            R.id.nav_logout -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
                return true
                //Handle logout
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }



    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
