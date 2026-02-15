package com.click.aifa.ui.dashBoard.fragments

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.click.aifa.R
import com.click.aifa.data.user.FamilyMemberEntity
import com.click.aifa.data.user.UserEntity
import com.click.aifa.data.user.UserSession
import com.click.aifa.data.user.UserWithFamily
import com.click.aifa.databinding.FragmentHomeBinding
import com.click.aifa.databinding.FragmentProfileBinding
import com.click.aifa.security.SecurePrefs
import com.click.aifa.ui.adapter.FamilyAdapter
import com.click.aifa.ui.dashBoard.HomeActivity
import com.click.aifa.ui.login.LoginActivity

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: FamilyAdapter
    private val familyList = mutableListOf<FamilyMemberEntity>()

    // 🔴 Normally comes from ViewModel / Session
    private val loggedInUserId = 1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentProfileBinding.bind(view)

        // 🔹 MOCK USER (replace with Room ViewModel)
        val user = UserSession.currentUser?.user

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

        familyList.addAll(UserSession.currentUser?.familyMembers!!)
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
        val etRelation = dialog.findViewById<EditText>(R.id.etRelation)
        val etAge = dialog.findViewById<EditText>(R.id.etAge)
        val etOccupation = dialog.findViewById<EditText>(R.id.etOccupation)
        val btnSave = dialog.findViewById<Button>(R.id.btnSave)

        member?.let {
            etName.setText(it.name)
            etRelation.setText(it.relation)
            etAge.setText(it.age.toString())
            etOccupation.setText(it.occupation)
        }

        btnSave.setOnClickListener {

            if (member == null) {
                familyList.add(
                    FamilyMemberEntity(
                        userId = loggedInUserId,
                        name = etName.text.toString(),
                        relation = etRelation.text.toString(),
                        age = etAge.text.toString().toInt(),
                        occupation = etOccupation.text.toString()
                    )
                )
            } else {
                val index = familyList.indexOf(member)
                familyList[index] = member.copy(
                    name = etName.text.toString(),
                    relation = etRelation.text.toString(),
                    age = etAge.text.toString().toInt(),
                    occupation = etOccupation.text.toString()
                )
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
            val updatedUser = user.copy(
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
