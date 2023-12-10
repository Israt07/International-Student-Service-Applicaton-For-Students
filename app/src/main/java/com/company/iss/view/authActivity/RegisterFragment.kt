package com.company.iss.view.authActivity

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.company.iss.R
import com.company.iss.databinding.FragmentRegisterBinding
import com.company.iss.model.UserModel
import com.company.iss.utils.KeyboardManager
import com.company.iss.utils.LoadingDialog
import com.company.iss.utils.NetworkManager
import com.company.iss.utils.SharedPref
import com.company.iss.utils.showErrorToast
import com.company.iss.utils.showSuccessToast
import com.company.iss.utils.showWarningToast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.Calendar

class RegisterFragment : Fragment() {

    //Declaring variables
    private lateinit var binding: FragmentRegisterBinding

    private lateinit var genderList: Array<String>
    private var selectedGender = ""
    private lateinit var facultyList: Array<String>
    private var selectedFaculty = ""
    private lateinit var programList: Array<String>
    private var selectedProgram = ""
    private var selectedDateOfBirth = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        LoadingDialog.init(requireContext())
        SharedPref.init(requireContext())

        //back button click event
        binding.backIcon.setOnClickListener { findNavController().popBackStack() }

        binding.countryCodePicker.registerCarrierNumberEditText(binding.mobileNumberEdittext)

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

        facultyList = arrayOf("FKE", "FKEKK", "FKP", "FPTT", "FTKEE", "FTMK")

        val facultyItemAdapter = ArrayAdapter(requireContext(), R.layout.single_dropdown_item, facultyList)
        binding.facultySelectAutoCompleteTextview.setAdapter(facultyItemAdapter)

        binding.facultySelectAutoCompleteTextview.setOnClickListener {
            binding.facultySelectAutoCompleteTextview.setText("")
        }

        binding.facultySelectAutoCompleteTextview.doOnTextChanged { text, _, _, _ ->
            selectedFaculty = text.toString()
            updateView(selectedFaculty)
        }

        //Register button click event
        binding.registerButton.setOnClickListener { registerUser(it) }

        //login text click event
        binding.loginButton.setOnClickListener { findNavController().navigate(R.id.action_registerFragment_to_loginFragment) }

        val c = Calendar.getInstance()
        var startYear = c.get(Calendar.YEAR)
        var startMonth = c.get(Calendar.MONTH)
        var startDay = c.get(Calendar.DAY_OF_MONTH)

        //date of birth textview click event
        binding.dateOfBirthTextview.setOnClickListener {

            val datePickerDialog = DatePickerDialog(requireContext(),
                { view, selectedYear, monthOfYear, dayOfMonth ->
                    val selectedMonth = monthOfYear+1

                    selectedDateOfBirth = "$dayOfMonth/$selectedMonth/$selectedYear"

                    binding.dateOfBirthTextview.text = selectedDateOfBirth

                    startYear = selectedYear
                    startMonth = monthOfYear
                    startDay = dayOfMonth
                }, startYear, startMonth, startDay)

            datePickerDialog.datePicker.maxDate = System.currentTimeMillis()

            datePickerDialog.show()
        }

        return binding.root
    }

    private fun registerUser(view: View) {
        if (binding.emailEdittext.text.toString().trim().isEmpty()) {
            binding.emailEdittext.error = "Enter your email"
            binding.emailEdittext.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(binding.emailEdittext.text.toString().trim()).matches()) {
            binding.emailEdittext.error = "Enter valid email"
            binding.emailEdittext.requestFocus()
            return
        }
        if (binding.nameEdittext.text.toString().trim().isEmpty()) {
            binding.nameEdittext.error = "Enter your name"
            binding.nameEdittext.requestFocus()
            return
        }
        if (binding.nameEdittext.text.toString().trim().contains("0") or binding.nameEdittext.text.toString().trim().contains("1") or binding.nameEdittext.text.toString().trim().contains("2") or binding.nameEdittext.text.toString().trim().contains("3") or binding.nameEdittext.text.toString().trim().contains("4") or binding.nameEdittext.text.toString().trim().contains("5") or binding.nameEdittext.text.toString().trim().contains("6") or binding.nameEdittext.text.toString().trim().contains("7") or binding.nameEdittext.text.toString().trim().contains("8") or binding.nameEdittext.text.toString().trim().contains("9")) {
            binding.nameEdittext.error = "Number not allowed"
            binding.nameEdittext.requestFocus()
            return
        }
        if (selectedGender.isEmpty()) {
            requireContext().showWarningToast("Select your gender")
            return
        }
        if (!genderList.contains(selectedGender)) {
            requireContext().showWarningToast("Select valid gender")
            return
        }
        if (binding.matricNumberEdittext.text.toString().trim().isEmpty()) {
            binding.matricNumberEdittext.error = "Enter matric number"
            binding.matricNumberEdittext.requestFocus()
            return
        }
        if (binding.passwordEdittext.text.toString().isEmpty()) {
            binding.passwordEdittext.error = "Enter password"
            binding.passwordEdittext.requestFocus()
            return
        }
        if (binding.passwordEdittext.text.toString().trim().length < 6) {
            binding.passwordEdittext.error = "Must be 6 character"
            binding.passwordEdittext.requestFocus()
            return
        }
        if (binding.passwordEdittext.text.toString().trim().lowercase() == binding.nameEdittext.text.toString().trim().lowercase()) {
            binding.passwordEdittext.error = "Must be different from name"
            binding.passwordEdittext.requestFocus()
            return
        }
        if (!Regex(".*[A-Z].*").containsMatchIn(binding.passwordEdittext.text.toString().trim())) {
            binding.passwordEdittext.error = "Password should have at least 1 uppercase letter"
            binding.passwordEdittext.requestFocus()
            return
        }
        if (!Regex(".*[a-z].*").containsMatchIn(binding.passwordEdittext.text.toString().trim())) {
            binding.passwordEdittext.error = "Password should have at least 1 lowercase letter"
            binding.passwordEdittext.requestFocus()
            return
        }
        if (!Regex(".*[0-9].*").containsMatchIn(binding.passwordEdittext.text.toString().trim())) {
            binding.passwordEdittext.error = "Password should have at least 1 number"
            binding.passwordEdittext.requestFocus()
            return
        }
        if (!Regex(".*[@#\$%^&+=].*").containsMatchIn(binding.passwordEdittext.text.toString().trim())) {
            binding.passwordEdittext.error = "Password should have at least 1 special character"
            binding.passwordEdittext.requestFocus()
            return
        }
        if (binding.mobileNumberEdittext.text.toString().trim().isEmpty()) {
            binding.mobileNumberEdittext.error = "Enter mobile number"
            binding.mobileNumberEdittext.requestFocus()
            return
        }
        if (selectedFaculty.isEmpty()) {
            requireContext().showWarningToast("Select your faculty")
            return
        }
        if (!facultyList.contains(selectedFaculty)) {
            requireContext().showWarningToast("Select valid faculty")
            return
        }
        if (selectedProgram.isEmpty()) {
            requireContext().showWarningToast("Select your program")
            return
        }
        if (!programList.contains(selectedProgram)) {
            requireContext().showWarningToast("Select valid program")
            return
        }
        if (selectedDateOfBirth.trim().isEmpty()) {
            requireContext().showWarningToast("Select date of birth")
            return
        }
        if (binding.passportNumberEdittext.text.toString().trim().isEmpty()) {
            binding.passportNumberEdittext.error = "Enter passport number"
            binding.passportNumberEdittext.requestFocus()
            return
        }

        if (NetworkManager.isInternetAvailable(requireContext())) {
            LoadingDialog.setText("Creating account...")
            LoadingDialog.show()

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(binding.emailEdittext.text.toString().trim(), binding.passwordEdittext.text.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = FirebaseAuth.getInstance().currentUser?.uid

                        val userModel = UserModel(userId, binding.nameEdittext.text.toString().trim(), selectedGender, binding.matricNumberEdittext.text.toString().trim(), binding.emailEdittext.text.toString().trim(), null, "STUDENT", binding.countryCodePicker.fullNumberWithPlus, selectedFaculty, selectedProgram, selectedDateOfBirth, binding.countryNamePicker.selectedCountryName, binding.passportNumberEdittext.text.toString().trim(), null, null)

                        //adding data to firebase
                        Firebase.database.reference.child("users").child(userId!!)
                            .setValue(userModel).addOnCompleteListener { task2 ->
                                if (task2.isSuccessful) {
                                    requireContext().showSuccessToast("Successful")
                                    LoadingDialog.dismiss()
                                    KeyboardManager.hideKeyBoard(requireContext(), view)
                                    FirebaseAuth.getInstance().signOut()
                                    findNavController().popBackStack()
                                } else {
                                    requireContext().showErrorToast(task2.exception?.localizedMessage.toString())
                                    LoadingDialog.dismiss()
                                }
                            }.addOnFailureListener{ e ->
                                requireContext().showErrorToast(e.localizedMessage!!.toString())
                                LoadingDialog.dismiss()
                            }
                    } else {
                        if (task.exception is FirebaseAuthUserCollisionException) {
                            binding.emailEdittext.error = "Email already registered"
                            binding.emailEdittext.requestFocus()
                        } else {
                            requireContext().showErrorToast(task.exception?.localizedMessage.toString())
                        }

                        LoadingDialog.dismiss()
                    }
                }
        } else {
            requireContext().showWarningToast("No internet available")
        }
    }

    private fun updateView(faculty: String) {
        programList = arrayOf()
        when(faculty) {
            "FKE" -> {
                programList = arrayOf("BEKG", "BEKM", "DEK")
                binding.programCardView.visibility = View.VISIBLE
            }
            "FKEKK" -> {
                programList = arrayOf("BENG", "BENR", "DEN")
                binding.programCardView.visibility = View.VISIBLE
            }
            "FKP" -> {
                programList = arrayOf("BMFG", "BMFI", "DMF")
                binding.programCardView.visibility = View.VISIBLE
            }
            "FPTT" -> {
                programList = arrayOf("BTEC", "BTMM", "BTMI", "BTMS")
                binding.programCardView.visibility = View.VISIBLE
            }
            "FTKEE" -> {
                programList = arrayOf("BEEI", "BEEA", "BEEY", "BEET", "BEEE", "BEEC", "BEEZ", "BEEM", "BEEL")
                binding.programCardView.visibility = View.VISIBLE
            }
            "FTMK" -> {
                programList = arrayOf("BITS", "BITC", "BITD", "BITI", "BITM", "BITZ", "BITE", "DCS")
                binding.programCardView.visibility = View.VISIBLE
            }
            else -> binding.programCardView.visibility = View.GONE
        }

        val programItemAdapter = ArrayAdapter(requireContext(), R.layout.single_dropdown_item, programList)
        binding.programSelectAutoCompleteTextview.setAdapter(programItemAdapter)

        binding.programSelectAutoCompleteTextview.setOnClickListener {
            binding.programSelectAutoCompleteTextview.setText("")
        }

        binding.programSelectAutoCompleteTextview.doOnTextChanged { text, _, _, _ ->
            selectedProgram = text.toString()
        }
    }
}