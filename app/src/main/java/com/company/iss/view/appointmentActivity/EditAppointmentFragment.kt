package com.company.iss.view.appointmentActivity

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
import com.company.iss.databinding.FragmentEditAppointmentBinding
import com.company.iss.model.AppointmentModel
import com.company.iss.utils.KeyboardManager
import com.company.iss.utils.LoadingDialog
import com.company.iss.utils.NetworkManager
import com.company.iss.utils.showErrorToast
import com.company.iss.utils.showSuccessToast
import com.company.iss.utils.showWarningToast
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class EditAppointmentFragment : Fragment() {

    //Declaring Variables
    private lateinit var binding: FragmentEditAppointmentBinding

    private var appointmentId = ""
    private var userId = ""
    private var name = ""
    private var matricNumber = ""
    private var email = ""
    private var faculty = ""
    private var lecturer = ""
    private var date = ""
    private var time = ""
    private var appointmentStatus = ""
    private var rejectionReason = ""

    private lateinit var timeList: Array<String>
    private var selectedTime = ""

    private lateinit var appointmentModel: AppointmentModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEditAppointmentBinding.inflate(inflater, container, false)
        LoadingDialog.init(requireContext())

        appointmentId = arguments?.getString("APPOINTMENT_ID").toString()
        userId = arguments?.getString("USER_ID").toString()
        name = arguments?.getString("NAME").toString()
        matricNumber = arguments?.getString("MATRIC_NUMBER").toString()
        email = arguments?.getString("EMAIL").toString()
        faculty = arguments?.getString("FACULTY").toString()
        lecturer = arguments?.getString("LECTURER").toString()
        date = arguments?.getString("DATE").toString()
        time = arguments?.getString("TIME").toString()
        appointmentStatus = arguments?.getString("APPOINTMENT_STATUS").toString()
        rejectionReason = arguments?.getString("REJECTION_REASON").toString()

        appointmentModel = AppointmentModel(appointmentId, userId, name, matricNumber, email, faculty, lecturer, date, time, appointmentStatus, rejectionReason)

        //set details
        binding.selectDateButton.text = date
        binding.timeSelectAutoCompleteTextview.setText(time)
        selectedTime = time

        //back button click event
        binding.backIcon.setOnClickListener { findNavController().popBackStack() }

        val c = appointmentModel.date?.split("/")
        var startYear = c?.get(2)?.toInt()
        var startMonth = c?.get(1)?.toInt()
        var startDay = c?.get(0)?.toInt()

        //select date button click event
        binding.selectDateButton.setOnClickListener {
            val datePickerDialog = DatePickerDialog(requireContext(),
                { _, selectedYear, monthOfYear, dayOfMonth ->
                    val selectedMonth = monthOfYear+1

                    appointmentModel.date = "$dayOfMonth/$selectedMonth/$selectedYear"

                    binding.selectDateButton.text = appointmentModel.date

                    startYear = selectedYear
                    startMonth = monthOfYear
                    startDay = dayOfMonth
                }, startYear!!, startMonth!!, startDay!!
            )

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

        //update button click event
        binding.updateAppointmentButton.setOnClickListener {
            updateAppointment(it)
        }

        return binding.root
    }

    private fun updateAppointment(view: View) {
        if (selectedTime.isEmpty()) {
            requireContext().showWarningToast("Select time")
            return
        }
        if (!timeList.contains(selectedTime)) {
            requireContext().showWarningToast("Select valid time")
            return
        }

        appointmentModel.time = selectedTime

        if (NetworkManager.isInternetAvailable(requireContext())) {
            LoadingDialog.setText("Updating...")
            LoadingDialog.show()

            Firebase.database.reference.child("appointments").child(appointmentId)
                .setValue(appointmentModel).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        KeyboardManager.hideKeyBoard(requireContext(), view)
                        requireContext().showSuccessToast("Update successful")
                        findNavController().popBackStack()
                    } else {
                        requireContext().showErrorToast("Something wrong.")
                    }
                    LoadingDialog.dismiss()
                }
        } else {
            requireContext().showWarningToast("No internet available")
        }
    }
}