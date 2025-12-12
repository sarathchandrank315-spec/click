package com.click.aifa.ui.register

import android.content.DialogInterface
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.click.aifa.R
import com.click.aifa.databinding.ActivityRegisterBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val familyList = mutableListOf<FamilyMember>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val genderList = listOf("Male", "Female", "Other")

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
            val name = binding.etName.text
            MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.add_family_member_title))
                .setMessage(getString(R.string.add_family_member_message, name))
                .setPositiveButton(getString(R.string.yes)) { dialog, _ -> registerFamily(dialog) }
                .setNegativeButton(getString(R.string.no)) { dialog, _ -> goToDashBoard(dialog) }
                .show()
        }
    }

    private fun registerFamily(dialog: DialogInterface) {
        showFamilyDialog()
        dialog.dismiss()
    }

    private fun goToDashBoard(dialog: DialogInterface) {
        dialog.dismiss()
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
            FamilyMember(
                name = name,
                relation = relation,
                age = age.toIntOrNull() ?: 0,
                occupation = occupation
            )
        )
    }

}
