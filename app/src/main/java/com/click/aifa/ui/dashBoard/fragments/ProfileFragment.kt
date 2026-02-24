package com.click.aifa.ui.dashBoard.fragments

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.click.aifa.R
import com.click.aifa.data.user.AppDatabase
import com.click.aifa.data.user.FamilyMemberEntity
import com.click.aifa.data.user.UserDao
import com.click.aifa.data.user.UserEntity
import com.click.aifa.data.user.UserSession
import com.click.aifa.data.user.UserWithFamily
import com.click.aifa.databinding.FragmentHomeBinding
import com.click.aifa.databinding.FragmentProfileBinding
import com.click.aifa.security.SecurePrefs
import com.click.aifa.ui.adapter.FamilyAdapter
import com.click.aifa.ui.dashBoard.HomeActivity
import com.click.aifa.ui.login.LoginActivity
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: FamilyAdapter
    private lateinit var dao: UserDao
    private lateinit var db: AppDatabase
    private val familyList = mutableListOf<FamilyMemberEntity>()

    // 🔴 Normally comes from ViewModel / Session
    private val loggedInUserId = 1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentProfileBinding.bind(view)

        // 🔹 MOCK USER (replace with Room ViewModel)
        val user = UserSession.currentUser?.user
        db = AppDatabase.getDatabase(requireContext())
        dao = db.userDao()
        // ---------- USER DATA ----------
        binding.tvName.text = "Name: ${user?.name}"
        binding.tvPhone.text = "Phone: ${user?.phone}"
        binding.tvAge.text = "Age: ${user?.age}"
        binding.tvOccupation.text = "Occupation: ${user?.occupation}"

        // ---------- BIOMETRIC SWITCH ----------
        binding.switchBiometric.isChecked =
            SecurePrefs.isBiometricEnabled(requireContext())

        binding.switchBiometric.setOnCheckedChangeListener { _, isChecked ->
            SecurePrefs.enableBiometric(requireContext(), isChecked)
        }

        // ---------- FAMILY LIST ----------
        UserSession.currentUser?.familyMembers?.let {
            familyList.addAll(it)
        }

        adapter = FamilyAdapter(familyList) {
            showFamilyDialog(it)
        }

        binding.rvFamily.layoutManager =
            LinearLayoutManager(requireContext())

        binding.rvFamily.adapter = adapter

        // ---------- ACTIONS ----------
        binding.btnAddFamily.setOnClickListener {
            showFamilyDialog(null)
        }
        binding.btnSignOut.setOnClickListener {
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finishAffinity()
        }
        binding.btnEditUser.setOnClickListener {
            showEditUserDialog(user!!)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showFamilyDialog(member: FamilyMemberEntity?) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_family)

        val etName = dialog.findViewById<EditText>(R.id.etName)
        val etAge = dialog.findViewById<EditText>(R.id.etAge)
        val etOccupation = dialog.findViewById<EditText>(R.id.etOccupation)
        val btnSave = dialog.findViewById<Button>(R.id.btnSave)
        val etRelation = dialog.findViewById<MaterialAutoCompleteTextView>(R.id.txtRelation)
        // Dropdown for relations
        val relations =
            listOf(
                "Father", "Mother", "Brother", "Sister", "Wife", "Husband", "Son", "Daughter"
            )
        etRelation.setText(relations[0])
        etRelation.setAdapter(
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                relations
            )
        )
        etRelation.keyListener = null      // disable typing
        etRelation.setOnClickListener {
            etRelation.showDropDown()       // force show dropdown
        }
        member?.let {
            etName.setText(it.name)
            etRelation.setText(it.relation)
            etAge.setText(it.age.toString())
            etOccupation.setText(it.occupation)
        }

        btnSave.setOnClickListener {

            val name = etName.text.toString().trim()
            val relation = etRelation.text.toString().trim()
            val ageText = etAge.text.toString().trim()
            val occupation = etOccupation.text.toString().trim()

            // 🔹 Name validation
            if (name.isEmpty()) {
                etName.error = "Enter name"
                etName.requestFocus()
                return@setOnClickListener
            }

            // 🔹 Relation validation
            if (relation.isEmpty()) {
                etRelation.error = "Select relation"
                etRelation.requestFocus()
                return@setOnClickListener
            }

            // 🔹 Age validation
            if (ageText.isEmpty()) {
                etAge.error = "Enter age"
                etAge.requestFocus()
                return@setOnClickListener
            }

            val age = ageText.toIntOrNull()
            if (age == null || age <= 0 || age > 120) {
                etAge.error = "Enter valid age"
                etAge.requestFocus()
                return@setOnClickListener
            }

            // 🔹 Occupation validation
            if (occupation.isEmpty()) {
                etOccupation.error = "Enter occupation"
                etOccupation.requestFocus()
                return@setOnClickListener
            }

            // ✅ If all valid, proceed

            if (member == null) {
                familyList.add(
                    FamilyMemberEntity(
                        userId = loggedInUserId,
                        name = name,
                        relation = relation,
                        age = age,
                        occupation = occupation
                    )
                )
            } else {
                val index = familyList.indexOf(member)
                familyList[index] = member.copy(
                    name = name,
                    relation = relation,
                    age = age,
                    occupation = occupation
                )
            }

            lifecycleScope.launch(Dispatchers.IO) {
                dao.insertFamilyMembers(familyList)
            }

            adapter.notifyDataSetChanged()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showEditUserDialog(user: UserEntity) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_user)

        val etName = dialog.findViewById<EditText>(R.id.etName)
        val etAge = dialog.findViewById<EditText>(R.id.etAge)
        val etOccupation = dialog.findViewById<EditText>(R.id.etOccupation)
        val btnSave = dialog.findViewById<Button>(R.id.btnSave)

        etName.setText(user.name)
        etAge.setText(user.age.toString())
        etOccupation.setText(user.occupation)

        btnSave.setOnClickListener {
            user.copy(
                name = etName.text.toString(),
                age = etAge.text.toString().toInt(),
                occupation = etOccupation.text.toString()
            )

            // 🔴 Save via ViewModel → Room
            dialog.dismiss()
        }

        dialog.show()
    }
}
