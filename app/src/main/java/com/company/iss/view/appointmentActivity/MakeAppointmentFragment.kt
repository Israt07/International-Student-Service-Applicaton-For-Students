package com.company.iss.view.appointmentActivity

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.company.iss.R
import com.company.iss.databinding.DialogAppointmentSuccessfulBinding
import com.company.iss.databinding.FragmentMakeAppointmentBinding
import com.company.iss.model.AppointmentModel
import com.company.iss.repository.MakeAppointmentRepository
import com.company.iss.utils.KeyboardManager
import com.company.iss.utils.LoadingDialog
import com.company.iss.utils.NetworkManager
import com.company.iss.utils.SharedPref
import com.company.iss.utils.showErrorToast
import com.company.iss.utils.showWarningToast
import com.company.iss.view_model.MakeAppointmentViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.Calendar

class MakeAppointmentFragment : Fragment() {

    //Declaring variables
    private lateinit var binding: FragmentMakeAppointmentBinding

    private lateinit var repository: MakeAppointmentRepository
    private lateinit var viewModel: MakeAppointmentViewModel

    private var lecturerList = ArrayList<String>()
    private var selectedLecturer = ""
    private lateinit var facultyList: Array<String>
    private var selectedFaculty = ""
    private var selectedDate = ""
    private lateinit var timeList: Array<String>
    private var selectedTime = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMakeAppointmentBinding.inflate(inflater, container, false)
        SharedPref.init(requireContext())
        LoadingDialog.init(requireContext())

        binding.progressbar.visibility = View.GONE

        repository = MakeAppointmentRepository()
        viewModel = ViewModelProvider(this, MakeAppointmentViewModelFactory(repository))[MakeAppointmentViewModel::class.java]

        observerList()

        //back button click event
        binding.backIcon.setOnClickListener { findNavController().popBackStack() }

        //set details
        facultyList = arrayOf("FKE", "FKEKK", "FKP", "FPTT", "FTKEE", "FTMK")

        val facultyItemAdapter = ArrayAdapter(requireContext(), R.layout.single_dropdown_item, facultyList)
        binding.facultySelectAutoCompleteTextview.setAdapter(facultyItemAdapter)

        binding.facultySelectAutoCompleteTextview.setOnClickListener {
            binding.facultySelectAutoCompleteTextview.setText("")
        }

        binding.facultySelectAutoCompleteTextview.doOnTextChanged { text, _, _, _ ->
            selectedFaculty = text.toString()

            binding.lecturerSelectTextInputLayout.visibility = View.GONE
            binding.progressbar.visibility = View.VISIBLE
            //request for data
            viewModel.requestLecturerList()
        }

        binding.lecturerSelectAutoCompleteTextview.setOnClickListener {
            binding.lecturerSelectAutoCompleteTextview.setText("")
        }

        binding.lecturerSelectAutoCompleteTextview.doOnTextChanged { text, _, _, _ ->
            selectedLecturer = text.toString()
        }

        val c = Calendar.getInstance()
        var startYear = c.get(Calendar.YEAR)
        var startMonth = c.get(Calendar.MONTH)
        var startDay = c.get(Calendar.DAY_OF_MONTH)

        //select date button click event
        binding.selectDateButton.setOnClickListener {
            val datePickerDialog = DatePickerDialog(requireContext(),
                { view, selectedYear, monthOfYear, dayOfMonth ->
                    val selectedMonth = monthOfYear+1

                    selectedDate = "$dayOfMonth/$selectedMonth/$selectedYear"

                    binding.selectDateButton.text = selectedDate

                    startYear = selectedYear
                    startMonth = monthOfYear
                    startDay = dayOfMonth
                }, startYear, startMonth, startDay)

            datePickerDialog.datePicker.minDate = System.currentTimeMillis()

            datePickerDialog.show()
        }

        timeList = arrayOf("9am-10am", "10am-11am", "11am-12am", "2pm-3pm", "3pm-4pm", "4pm-5pm", "5pm-6pm")

        val timeItemAdapter = ArrayAdapter(requireContext(), R.layout.single_dropdown_item, timeList)
        binding.timeSelectAutoCompleteTextview.setAdapter(timeItemAdapter)

        binding.timeSelectAutoCompleteTextview.setOnClickListener {
            binding.timeSelectAutoCompleteTextview.setText("")
        }

        binding.timeSelectAutoCompleteTextview.doOnTextChanged { text, _, _, _ ->
            selectedTime = text.toString()
        }

        //make appointment button click event
        binding.makeAppointmentButton.setOnClickListener { makeAppointment(it) }

        return binding.root
    }

    private fun observerList() {
        viewModel.lecturerListLiveData.observe(viewLifecycleOwner) {
            lecturerList.clear()
            if (it != null) {
                val filteredLecturer = it.filter { item-> item.faculty == selectedFaculty }
                ArrayList(filteredLecturer).forEach { item->
                    lecturerList.add(item.name.toString())
                }
                if (lecturerList.isEmpty()) {
                    binding.lecturerSelectAutoCompleteTextview.setText("")
                }
            } else {
                binding.lecturerSelectAutoCompleteTextview.setText("")
            }

            val lecturerItemAdapter = ArrayAdapter(requireContext(), R.layout.single_dropdown_item, lecturerList)
            binding.lecturerSelectAutoCompleteTextview.setAdapter(lecturerItemAdapter)

            binding.lecturerSelectTextInputLayout.visibility = View.VISIBLE
            binding.progressbar.visibility = View.GONE
        }
    }

    private fun makeAppointment(view: View) {
        if (selectedLecturer.isEmpty()) {
            requireContext().showWarningToast("Select lecturer")
            return
        }
        if (!lecturerList.contains(selectedLecturer)) {
            requireContext().showWarningToast("Select valid lecturer")
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
        if (selectedDate.isEmpty()) {
            requireContext().showWarningToast("Select date")
            return
        }
        if (selectedTime.isEmpty()) {
            requireContext().showWarningToast("Select time")
            return
        }
        if (!timeList.contains(selectedTime)) {
            requireContext().showWarningToast("Select valid time")
            return
        }

        if (NetworkManager.isInternetAvailable(requireContext())) {
            LoadingDialog.setText("Processing...")
            LoadingDialog.show()

            val appointmentId = Firebase.database.reference.child("appointments").push().key.toString()

            val appointmentModel = AppointmentModel(appointmentId, FirebaseAuth.getInstance().currentUser!!.uid, SharedPref.read("USER_NAME", ""), SharedPref.read("USER_MATRIC_NUMBER", ""), SharedPref.read("USER_EMAIL", ""), selectedFaculty, selectedLecturer, selectedDate, selectedTime, "pending", "")

            Firebase.database.reference.child("appointments").child(appointmentId)
                .setValue(appointmentModel).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        KeyboardManager.hideKeyBoard(requireContext(), view)
                        successDialog()
                        binding.lecturerSelectAutoCompleteTextview.setText("")
                        selectedLecturer = ""
                        binding.facultySelectAutoCompleteTextview.setText("")
                        selectedFaculty = ""
                        binding.selectDateButton.text = "Select Date"
                        selectedDate = ""
                        binding.timeSelectAutoCompleteTextview.setText("")
                        selectedTime = ""
                    } else {
                        requireContext().showErrorToast("Something wrong.")
                    }
                    LoadingDialog.dismiss()
                }
        } else {
            requireContext().showWarningToast("No internet available")
        }
    }

    private fun successDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val dialogBinding = DialogAppointmentSuccessfulBinding.inflate(layoutInflater)

        builder.setView(dialogBinding.root)
        builder.setCancelable(true)

        val alertDialog = builder.create()

        //make transparent to default dialog
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(0))

        dialogBinding.viewAppointmentsButton.setOnClickListener {
            findNavController().navigate(R.id.action_makeAppointmentFragment_to_viewAppointmentsFragment)
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

}



class MakeAppointmentViewModelFactory(private val repository: MakeAppointmentRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = MakeAppointmentViewModel(repository) as T
}