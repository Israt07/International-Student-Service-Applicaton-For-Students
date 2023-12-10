package com.company.iss.view.faqActivity.visaActivity

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.company.iss.R
import com.company.iss.databinding.FragmentVisaRenewBinding
import com.company.iss.model.VisaRenewModel
import com.company.iss.utils.KeyboardManager
import com.company.iss.utils.LoadingDialog
import com.company.iss.utils.NetworkManager
import com.company.iss.utils.SharedPref
import com.company.iss.utils.showErrorToast
import com.company.iss.utils.showSuccessToast
import com.company.iss.utils.showWarningToast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class VisaRenewFragment : Fragment() {

    //Declaring variables
    private lateinit var binding: FragmentVisaRenewBinding

    private lateinit var genderList: Array<String>
    private var selectedGender = ""
    private var selectedPassportExpiryDate = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentVisaRenewBinding.inflate(inflater, container, false)
        SharedPref.init(requireContext())
        LoadingDialog.init(requireContext())

        //back button click event
        binding.backIcon.setOnClickListener { findNavController().popBackStack() }

        //set details
        genderList = arrayOf("Male", "Female")

        val genderItemAdapter = ArrayAdapter(requireContext(), R.layout.single_dropdown_item, genderList)
        binding.genderSelectAutoCompleteTextview.setAdapter(genderItemAdapter)

        binding.genderSelectAutoCompleteTextview.setOnClickListener {
            binding.genderSelectAutoCompleteTextview.setText("")
        }

        binding.genderSelectAutoCompleteTextview.doOnTextChanged { text, _, _, _ ->
            selectedGender = text.toString()
        }

        val c = Calendar.getInstance()
        var startYear = c.get(Calendar.YEAR)
        var startMonth = c.get(Calendar.MONTH)
        var startDay = c.get(Calendar.DAY_OF_MONTH)

        //passport expiry date button click event
        binding.passportExpiryDateButton.setOnClickListener {
            val datePickerDialog = DatePickerDialog(requireContext(),
                { _, selectedYear, monthOfYear, dayOfMonth ->
                    val selectedMonth = monthOfYear+1

                    selectedPassportExpiryDate = "$dayOfMonth/$selectedMonth/$selectedYear"

                    binding.passportExpiryDateButton.text = selectedPassportExpiryDate

                    startYear = selectedYear
                    startMonth = monthOfYear
                    startDay = dayOfMonth
                }, startYear, startMonth, startDay)

            datePickerDialog.show()
        }

        //submit button click event
        binding.submitButton.setOnClickListener { submitForm(it) }

        return binding.root
    }

    private fun submitForm(view: View) {
        if (binding.typeOfPassportEdittext.text.toString().trim().isEmpty()) {
            binding.typeOfPassportEdittext.error = "Enter type of passport"
            binding.typeOfPassportEdittext.requestFocus()
            return
        }
        if (binding.fullNameEdittext.text.toString().trim().isEmpty()) {
            binding.fullNameEdittext.error = "Enter full name"
            binding.fullNameEdittext.requestFocus()
            return
        }
        if (selectedGender.isEmpty()) {
            requireContext().showWarningToast("Select Gender")
            return
        }
        if (!genderList.contains(selectedGender)) {
            requireContext().showWarningToast("Select valid gender")
            return
        }
        if (binding.nationalityEdittext.text.toString().trim().isEmpty()) {
            binding.nationalityEdittext.error = "Enter nationality"
            binding.nationalityEdittext.requestFocus()
            return
        }
        if (binding.passportNumberEdittext.text.toString().trim().isEmpty()) {
            binding.passportNumberEdittext.error = "Enter passport number"
            binding.passportNumberEdittext.requestFocus()
            return
        }
        if (selectedPassportExpiryDate.trim().isEmpty()) {
            requireContext().showWarningToast("Selected passport expiry date")
            return
        }

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = Date()
        val currentDate = dateFormat.format(date)

        if (NetworkManager.isInternetAvailable(requireContext())) {
            KeyboardManager.hideKeyBoard(requireContext(), view)
            LoadingDialog.setText("Processing...")
            LoadingDialog.show()

            val visaRenewId = Firebase.database.reference.child("visa_renew_forms").push().key.toString()

            val visaRenewModel = VisaRenewModel(visaRenewId, FirebaseAuth.getInstance().currentUser!!.uid, binding.typeOfPassportEdittext.text.toString().trim(), binding.fullNameEdittext.text.toString().trim(), selectedGender, binding.nationalityEdittext.text.toString().trim(), binding.passportNumberEdittext.text.toString().trim(), selectedPassportExpiryDate, currentDate)

            Firebase.database.reference.child("visa_renew_forms").child(visaRenewId)
                .setValue(visaRenewModel).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        requireContext().showSuccessToast("Successful")
                    } else {
                        requireContext().showErrorToast("Something wrong.")
                    }
                    LoadingDialog.dismiss()
                    findNavController().popBackStack()
                }
        } else {
            requireContext().showWarningToast("No internet available")
        }
    }
}