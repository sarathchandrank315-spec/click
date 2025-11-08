package com.click.aifa.ui.register

import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.click.aifa.R
import com.click.aifa.databinding.ActivityRegisterBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        dialog.dismiss()
    }

    private fun goToDashBoard(dialog: DialogInterface) {
        dialog.dismiss()
    }
}
