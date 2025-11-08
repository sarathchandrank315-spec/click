package com.click.aifa.ui.register

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.click.aifa.databinding.ActivityRegisterFamilyBinding

class FamilyRegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterFamilyBinding
    private val familyList = mutableListOf<FamilyMember>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterFamilyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAddPerson.setOnClickListener { addPerson() }
        binding.btnComplete.setOnClickListener { completeRegistration() }
    }

    private fun addPerson() {
        val name = binding.etName.text.toString().trim()
        val relation = binding.etRelation.text.toString().trim()
        val occupation = binding.etOccupation.text.toString().trim()
        val ageStr = binding.etAge.text.toString().trim()

        if (name.isEmpty() || relation.isEmpty() || occupation.isEmpty() || ageStr.isEmpty()) {
            Toast.makeText(this, "Please fill all details", Toast.LENGTH_SHORT).show()
            return
        }

        val age = ageStr.toIntOrNull()
        if (age == null || age <= 0) {
            Toast.makeText(this, "Please enter a valid age", Toast.LENGTH_SHORT).show()
            return
        }

        // Add to model list
        val member = FamilyMember(name, relation, occupation, age)
        familyList.add(member)

        Toast.makeText(this, "${member.name} added successfully!", Toast.LENGTH_SHORT).show()

        // Clear fields and move cursor back to Name
        binding.etName.text.clear()
        binding.etRelation.text.clear()
        binding.etOccupation.text.clear()
        binding.etAge.text.clear()
        binding.etName.requestFocus()
    }

    private fun completeRegistration() {
        if (familyList.isEmpty()) {
            Toast.makeText(this, "No family members added yet!", Toast.LENGTH_SHORT).show()
            return
        }

        // Example: Show all added members in a Toast (you can save to DB or navigate)
        val allMembers = familyList.joinToString("\n") {
            "${it.name} - ${it.relation}, ${it.occupation}, Age ${it.age}"
        }
        Toast.makeText(this, "Family Registered:\n$allMembers", Toast.LENGTH_LONG).show()
    }
}

// Model class to hold family member data
data class FamilyMember(
    val name: String,
    val relation: String,
    val occupation: String,
    val age: Int
)

