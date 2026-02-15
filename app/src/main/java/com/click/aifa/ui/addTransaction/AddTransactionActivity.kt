package com.click.aifa.ui.addTransaction

import android.Manifest
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.FileProvider
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.click.aifa.R
import com.click.aifa.databinding.ActivityAddBinding
import com.click.aifa.databinding.TopbarLayoutBinding
import com.click.aifa.ui.addTransaction.adapter.TransactionAdapter
import com.click.aifa.ui.addTransaction.addIncome.AddIncomeActivity
import com.click.aifa.viewmodel.IncomeViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.File

class AddTransactionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddBinding
    private lateinit var incomeViewModel: IncomeViewModel
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var cameraImageUri: Uri
    private val cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                showImagePickerDialog()
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
              processImage(cameraImageUri)
            }
        }

    val pickImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { processImage(it) }
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)
        customizeAppBar(binding.topBar)
        // RecyclerView setup
        // 1️⃣ Adapter
        transactionAdapter = TransactionAdapter()
        binding.recyclerTransactions.layoutManager = LinearLayoutManager(this)
        binding.recyclerTransactions.adapter =transactionAdapter
        // 3️⃣ ViewModel
        incomeViewModel = ViewModelProvider(this)[IncomeViewModel::class.java]

        // 4️⃣ Observe LiveData (Realtime updates)
        incomeViewModel.allIncomeList.observe(this) { list ->
            transactionAdapter.submitList(list)
        }

        binding.btnAddIncome.setOnClickListener {
            val intent = Intent(this, AddIncomeActivity::class.java)
            startActivity(intent)
        }
        binding.btnAddExpense.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle("Scan Bill")
                .setMessage("Do you want to scan bill?")
                .setPositiveButton(getString(R.string.yes)) { dialog, _ -> callScanner(dialog) }
                .setNegativeButton("Add Manually") { dialog, _ -> startAddExpenseActivity(
                   dialog= dialog
                ) }
                .show()
        }
    }

    private fun startAddExpenseActivity(
        dialog: DialogInterface?,
        amount: String? = null,
        date1: String? = null,
        time1: String? = null,
        category1: String? = null
    ) {
        dialog?.dismiss()

        val intent = Intent(this, AddIncomeActivity::class.java)

        intent.putExtra("IS_EXPENSE", true)
        intent.putExtra("AMOUNT", amount)
        intent.putExtra("DATE", date1)
        intent.putExtra("TIME", time1)
        intent.putExtra("CATEGORY", category1)

        startActivity(intent)
    }


    private fun callScanner(dialog: DialogInterface) {
        dialog.dismiss()
        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)



    }
    private fun createImageUri(): Uri {
        val image = File(cacheDir, "images")
        image.mkdirs()

        val file = File(image, "camera_image_${System.currentTimeMillis()}.jpg")
        return FileProvider.getUriForFile(
            this,
            "${packageName}.fileprovider",
            file
        )
    }

    private fun showImagePickerDialog() {
        val options = arrayOf("Camera", "Gallery")

        AlertDialog.Builder(this)
            .setTitle("Select Image")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        cameraImageUri = createImageUri()
                        takePictureLauncher.launch(cameraImageUri)
                    }
                    1 -> {
                        pickImage.launch("image/*")
                    }
                }
            }
            .show()
    }

    private fun processImage(uri: Uri) {
        val image = InputImage.fromFilePath(this, uri)

        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                extractDetails(visionText.text)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    fun extractDate(text: String): String? {
        val regex = Regex("\\b\\d{2}/\\d{2}/\\d{4}\\b")
        return regex.find(text)?.value
    }

    fun extractTime(text: String): String? {
        val regex = Regex("(\\d{1,2}:\\d{2}\\s*(AM|PM)?)", RegexOption.IGNORE_CASE)
        return regex.find(text)?.value
    }

    fun extractCategory(text: String): String {
        return when {
            text.contains("grocery", true) ||
                    text.contains("rice", true) ||
                    text.contains("milk", true) -> "Grocery"

            text.contains("petrol", true) ||
                    text.contains("fuel", true) -> "Fuel"

            text.contains("medicine", true) ||
                    text.contains("pharmacy", true) -> "Medical"

            text.contains("restaurant", true) ||
                    text.contains("food", true) -> "Food & Dining"

            text.contains("WEDDINGS", true) ||
                    text.contains("SAREE", true)||
                    text.contains("COTTON", true)||
                    text.contains("MUNDU", true) -> "Shopping"

            else -> "Unknown"
        }
    }

    fun extractTotalAmount(text: String): String? {
        val patterns = listOf(
            "total[:\\s]*([0-9]+\\.?[0-9]*)",
            "amount payable[:\\s]*([0-9]+\\.?[0-9]*)",
            "MRP TOTAL \\s*([0-9]+\\.?[0-9]*)",
            "rs\\.\\s*([0-9]+\\.?[0-9]*)",
            "grand total[:\\s]*([0-9]+\\.?[0-9]*)"
        )
        Log.d("TAG", "extractTotalAmount::text--$text ")
        for (pattern in patterns) {
            Log.d("TAG", "extractTotalAmount:$pattern ")
            val regex = Regex(pattern, RegexOption.IGNORE_CASE)
            val match = regex.find(text)
            if (match != null) return match.groupValues[1]
        }
        // 🔹 Step 2: If no pattern matched, find all numbers
        val numberRegex = Regex("\\d+\\.?\\d*")
        val numbers = numberRegex.findAll(text)
            .map { it.value.toDoubleOrNull() }
            .filterNotNull()
            .toList()

        // 🔹 Step 3: Return largest number if exists
        return numbers.maxOrNull()?.toString()
    }

    fun extractDetails(text: String) {
        val amount = extractTotalAmount(text)
        val date = extractDate(text)
        val time = extractTime(text)
        val category = extractCategory(text)

        Log.d(
            "SCAN_RESULT", """
        Amount: $amount
        Date: $date
        Time: $time
        Category: $category
    """.trimIndent()
        )
        startAddExpenseActivity(null,amount,date,time,category)
    }


    private fun customizeAppBar(topBar: TopbarLayoutBinding) {
        topBar.leftButton.setImageDrawable(
            AppCompatResources.getDrawable(
                this,
                R.drawable.ic_back
            )
        )
        topBar.btnMenu.setOnClickListener {
            finish()
        }
        topBar.btnNotification.visibility = View.INVISIBLE
        topBar.tvTitle.text = "Transactions"
    }

}
