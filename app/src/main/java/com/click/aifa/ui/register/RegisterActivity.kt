package com.click.aifa.ui.register

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.TopAppBar
import androidx.lifecycle.lifecycleScope
import com.click.aifa.R
import com.click.aifa.data.User
import com.click.aifa.data.user.AppDatabase
import com.click.aifa.data.user.FamilyMemberEntity
import com.click.aifa.data.user.UserDao
import com.click.aifa.data.user.UserEntity
import com.click.aifa.databinding.ActivityRegisterBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterActivity : AppCompatActivity() {

    private lateinit var dao: UserDao
    private lateinit var db: AppDatabase
    private lateinit var binding: ActivityRegisterBinding
    private val familyList = mutableListOf<FamilyMemberEntity>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val genderList = listOf("Male", "Female", "Other")
        db = AppDatabase.getDatabase(this)
        dao = db.userDao()
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            genderList
        )

        binding.etGender.setAdapter(adapter)
        binding.etGender.keyListener = null      // disable typing
        binding.etGender.setOnClickListener {
            binding.etGender.showDropDown()       // force show dropdown
        }
        // Example action
        binding.btnRegister.setOnClickListener {
            if (!validateFields())
                return@setOnClickListener
            if (familyList.isEmpty()) {
                val name = binding.etName.text
                MaterialAlertDialogBuilder(this)
                    .setTitle(getString(R.string.add_family_member_title))
                    .setMessage(getString(R.string.add_family_member_message, name))
                    .setPositiveButton(getString(R.string.yes)) { dialog, _ -> registerFamily(dialog) }
                    .setNegativeButton(getString(R.string.no)) { dialog, _ -> goToDashBoard(dialog) }
                    .show()
            } else {
                registerUser()
            }

        }

    }

    private fun validateFields(): Boolean {
        val name = binding.etName.text.toString()
        val phone = binding.etPhone.text.toString()
        val ageText = binding.etAge.text.toString()
        val occupation = binding.etOccupation.text.toString()
        val password = binding.etPassword.text.toString()
        val confirmPassword = binding.etConfirmPassword.text.toString()
        // ✔ Convert age safely
        val age = ageText.toIntOrNull()
        // 🔴 Validate empty fields
        when {

            phone.isEmpty() -> {
                binding.etPhone.error = "Phone is required"
                return false
            }

            name.isEmpty() -> {
                binding.etName.error = "Name is required"
                return false
            }

            ageText.isEmpty() -> {
                binding.etAge.error = "Age is required"
                return false
            }

            occupation.isEmpty() -> {
                binding.etOccupation.error = "Occupation is required"
                return false
            }

            password.isEmpty() -> {
                binding.etPassword.error = "Password is required"
                return false
            }

            confirmPassword.isEmpty() -> {
                binding.etConfirmPassword.error = "Confirm password"
                return false
            }

            password != confirmPassword -> {
                binding.etConfirmPassword.error = "Passwords do not match"
                return false
            }
        }


        // Phone validation
        if (phone.isEmpty()) {
            binding.etPhone.error = "Phone number required"
            binding.etPhone.requestFocus()
            return false
        }

        if (phone.length != 10 || !phone.all { it.isDigit() }) {
            binding.etPhone.error = "Enter valid 10-digit phone number"
            binding.etPhone.requestFocus()
            return false
        }

        if (age == null) {
            binding.etAge.error = "Enter valid age"
            binding.etAge.requestFocus()
            return false
        }

        if (age < 18) {
            binding.etAge.error = "You must be above 18"
            binding.etAge.requestFocus()
            return false
        }
        return true

    }

    private fun registerUser() {
        val name = binding.etName.text.toString()
        val phone = binding.etPhone.text.toString()
        val ageText = binding.etAge.text.toString()
        val occupation = binding.etOccupation.text.toString()
        val password = binding.etPassword.text.toString()
        binding.etConfirmPassword.text.toString()
        val age = ageText.toInt()
        val user = UserEntity(
            age = age,
            name = name,
            phone = phone,
            password = password,
            occupation = occupation
        )
        insertUserToDatabase(user)
    }

    private fun registerFamily(dialog: DialogInterface) {
        showFamilyDialog()
        dialog.dismiss()
    }

    private fun goToDashBoard(dialog: DialogInterface) {
        dialog.dismiss()
        registerUser()
    }

    private fun showFamilyDialog() {

        val dialogView = layoutInflater.inflate(R.layout.dialog_family_member, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val btnClose = dialogView.findViewById<ImageView>(R.id.btnClose)
        val btnAddAnother = dialogView.findViewById<Button>(R.id.btnAddAnother)
        val btnDone = dialogView.findViewById<Button>(R.id.btnDone)

        val etName = dialogView.findViewById<TextInputEditText>(R.id.etName)
        val etAge = dialogView.findViewById<TextInputEditText>(R.id.etAge)
        val etOccupation = dialogView.findViewById<TextInputEditText>(R.id.etOccupation)
        val etRelation = dialogView.findViewById<MaterialAutoCompleteTextView>(R.id.txtRelation)

        // Dropdown for relations
        val relations =
            listOf("Father", "Mother", "Brother", "Sister", "Wife", "Husband", "Son", "Daughter")
        etRelation.setAdapter(
            ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                relations
            )
        )

        etRelation.keyListener = null      // disable typing
        etRelation.setOnClickListener {
            etRelation.showDropDown()       // force show dropdown
        }

        btnClose.setOnClickListener { dialog.dismiss() }

        btnAddAnother.setOnClickListener {
            saveFamilyMember(etName, etRelation, etAge, etOccupation)
            etName.text?.clear()
            etRelation.setText("")
            etAge.text?.clear()
            etOccupation.text?.clear()
        }

        btnDone.setOnClickListener {
            saveFamilyMember(etName, etRelation, etAge, etOccupation)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun saveFamilyMember(
        etName: TextInputEditText,
        etRelation: AutoCompleteTextView,
        etAge: TextInputEditText,
        etOccupation: TextInputEditText
    ) {
        val name = etName.text.toString()
        val relation = etRelation.text.toString()
        val age = etAge.text.toString()
        val occupation = etOccupation.text.toString()

        if (name.isEmpty() || relation.isEmpty()) {
            Toast.makeText(this, "Enter required fields", Toast.LENGTH_SHORT).show()
            return
        }

        familyList.add(
            FamilyMemberEntity(
                name = name,
                relation = relation,
                age = age.toIntOrNull() ?: 0,
                userId = 0,
                occupation = occupation
            )
        )
    }

    private fun insertUserToDatabase(newUser: UserEntity) {

        lifecycleScope.launch(Dispatchers.IO) {

            // Insert user first
            val userId = try {
                dao.insertUser(newUser).toInt()
            } catch (e: android.database.sqlite.SQLiteConstraintException) {
                runOnUiThread {
                    Toast.makeText(this@RegisterActivity, "Phone number already exists", Toast.LENGTH_LONG).show()
                }
                 return@launch
            }
            // Update family members userId
            val updatedFamily = familyList.map {
                it.copy(userId = userId)
            }

            dao.insertFamilyMembers(updatedFamily)

            withContext(Dispatchers.Main) {
                Toast.makeText(
                    this@RegisterActivity,
                    "User registered successfully!",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }
}
