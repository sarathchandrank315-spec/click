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
import com.click.aifa.security.BiometricUtil
import com.click.aifa.security.SecurePrefs
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
        // 🔁 Auto-login using biometric
        if (SecurePrefs.isBiometricEnabled(this) &&
            BiometricUtil.isAvailable(this)
        ) {
            BiometricUtil.authenticate(
                this,
                onSuccess = {
                    autoLogin()
                },
                onError = {}
            )
        }
        setContentView(binding.root)
        db = AppDatabase.getDatabase(this)
        dao = db.userDao()
        SecurePrefs.getUsername(this@LoginActivity)?.let {
            binding.etPhone.setText(it)
        }

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
                        SecurePrefs.saveCredentials(this@LoginActivity, email, password)
                        SecurePrefs.enableBiometric(
                            this@LoginActivity,
                            binding.cbBiometric.isChecked
                        )
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

    private fun autoLogin() {
        lifecycleScope.launch(Dispatchers.IO) {

            val username = SecurePrefs.getUsername(this@LoginActivity)
            val password = SecurePrefs.getPassword(this@LoginActivity)

            if (username != null && password != null) {
                lifecycleScope.launch(Dispatchers.IO) {
                    val user = dao.getUserByPhone(username)
                    val fullUser = dao.getUserWithFamily(user?.id!!)
                    UserSession.login(fullUser!!)  // Save in session
                    // Use credentials for API session if needed
                    startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                    finish()
                }
            }
        }
    }
}
