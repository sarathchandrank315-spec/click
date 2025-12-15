package com.click.aifa.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.click.aifa.data.user.AppDatabase
import com.click.aifa.data.user.UserDao
import com.click.aifa.data.user.UserSession
import com.click.aifa.databinding.ActivityLoginBinding
import com.click.aifa.ui.dashBoard.HomeActivity
import com.click.aifa.ui.register.RegisterActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    // Declare binding object
    private lateinit var binding: ActivityLoginBinding

    private lateinit var dao: UserDao
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate layout using view binding
        installSplashScreen()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = AppDatabase.getDatabase(this)
        dao = db.userDao()
        // Handle Login button
        binding.btnLogin.setOnClickListener {
            val email = binding.etPhone.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()



            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()
            } else {
                checkLogin(email, password)
            }
        }

        // Handle Register button
        binding.btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun checkLogin(email: String, password: String) {
        lifecycleScope.launch(Dispatchers.IO) {

            val user = dao.getUserByPhone(email)

            withContext(Dispatchers.Main) {
                if (user == null) {
                    Toast.makeText(this@LoginActivity, "User not found", Toast.LENGTH_SHORT).show()
                } else if (user.password != password) {
                    Toast.makeText(this@LoginActivity, "Incorrect password", Toast.LENGTH_SHORT)
                        .show()
                } else {

                    // Load full user + family
                    lifecycleScope.launch(Dispatchers.IO) {
                        val fullUser = dao.getUserWithFamily(user.id)
                        UserSession.login(fullUser!!)  // Save in session

                        withContext(Dispatchers.Main) {
                            startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                            finish()
                        }
                    }

                }
            }
        }
    }
}
