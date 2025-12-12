package com.click.aifa.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.click.aifa.databinding.ActivityLoginBinding
import com.click.aifa.ui.dashBoard.HomeActivity
import com.click.aifa.ui.register.RegisterActivity

class LoginActivity : AppCompatActivity() {

    // Declare binding object
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate layout using view binding
        installSplashScreen()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Handle Login button
        binding.btnLogin.setOnClickListener {
            val email = binding.etPhone.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            when {
                email.isEmpty() || password.isEmpty() -> {
                    Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()
                }
                email == "admin@example.com" && password == "123456" -> {
                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                    // TODO: Navigate to home screen
                }
                else -> {
                    Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Handle Register button
        binding.btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}
