package com.click.aifa.ui.dashBoard


import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.click.aifa.R
import com.click.aifa.databinding.ActivityHomeBinding
import com.click.aifa.reminder.ReminderActivity
import com.click.aifa.ui.addTransaction.AddTransactionActivity
import com.click.aifa.ui.dashBoard.chatbot.ChatBotActivity
import com.click.aifa.ui.dashBoard.fragments.HomeFragment
import com.click.aifa.ui.dashBoard.fragments.ProfileFragment
import com.click.aifa.ui.dashBoard.fragments.StatisticsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val homeFragment = HomeFragment()
    private val statisticsFragment = StatisticsFragment()
    private val profileFragment = ProfileFragment()
    private var activeFragment: Fragment = homeFragment
    private var chatOpened = false
    private var lastSelectedTab = R.id.nav_home
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        customizeTopBar("HOME")
        WindowCompat.setDecorFitsSystemWindows(window, false)
        ViewCompat.setOnApplyWindowInsetsListener(binding.rootLayout) { view, insets ->
            val statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            view.setPadding(0, statusBar.top, 0, 0)
            insets
        }
        // Floating Action Button click
        binding.fabAdd.setOnClickListener {
            val intent = Intent(this, AddTransactionActivity::class.java)
            startActivity(intent)
        }
        binding.topBar.btnNotification.setOnClickListener {
            val intent = Intent(this, ReminderActivity::class.java)
            startActivity(intent)
        }
        loadFragments()
        // Bottom Nav item clicks
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    showFragment(homeFragment)
                    customizeTopBar("HOME")
                    lastSelectedTab = R.id.nav_home
                }

                R.id.nav_stats -> {
                    showFragment(statisticsFragment)
                    customizeTopBar("STATISTICS")
                    lastSelectedTab = R.id.nav_stats
                }

                R.id.nav_profile -> {
                    showFragment(profileFragment)
                    customizeTopBar("PROFILE")
                    lastSelectedTab = R.id.nav_profile
                }

                R.id.nav_wallet -> openChat()
            }
            true
        }
    }

    private fun customizeTopBar(string: String) {
        binding.topBar.tvTitle.text = string
        binding.topBar.leftButton.visibility= View.GONE
    }

    private fun openChat() {
        chatOpened = true
        val intent = Intent(this, ChatBotActivity::class.java)
        startActivity(intent)
    }

    private fun loadFragments() {
        // Add fragments once and hide others (for smooth tab switching)
        supportFragmentManager.beginTransaction().add(R.id.container, profileFragment, "3")
            .hide(profileFragment).commit()
        supportFragmentManager.beginTransaction().add(R.id.container, statisticsFragment, "2")
            .hide(statisticsFragment).commit()
        supportFragmentManager.beginTransaction().add(R.id.container, homeFragment, "1").commit()

    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .hide(activeFragment)
            .show(fragment)
            .commit()
        activeFragment = fragment
    }

    override fun onResume() {
        super.onResume()
        if (chatOpened) {
            chatOpened = false
            val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
            bottomNavigationView.selectedItemId = lastSelectedTab

        }
    }
}

