package com.company.iss.view.appointmentActivity

import android.app.AlertDialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.company.iss.R
import com.company.iss.adapter.ViewAppointmentsAdapter
import com.company.iss.databinding.DialogViewAppointmentBinding
import com.company.iss.databinding.FragmentViewAppointmentsBinding
import com.company.iss.interfaces.ViewAppointmentsItemClickListener
import com.company.iss.model.AppointmentModel
import com.company.iss.repository.ViewAppointmentsRepository
import com.company.iss.view_model.ViewAppointmentsViewModel
import com.google.firebase.auth.FirebaseAuth

class ViewAppointmentsFragment : Fragment(), ViewAppointmentsItemClickListener {

    //Declaring variables
    private lateinit var binding: FragmentViewAppointmentsBinding

    private lateinit var repository: ViewAppointmentsRepository
    private lateinit var viewModel: ViewAppointmentsViewModel

    private var appointmentsList = ArrayList<AppointmentModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentViewAppointmentsBinding.inflate(inflater, container, false)

        repository = ViewAppointmentsRepository()
        viewModel = ViewModelProvider(this, ViewAppointmentsViewModelFactory(repository))[ViewAppointmentsViewModel::class.java]

        //request for data
        viewModel.requestAppointmentsList(FirebaseAuth.getInstance().currentUser!!.uid)

        observerList()

        //back button click event
        binding.backIcon.setOnClickListener { findNavController().popBackStack() }

        //set recyclerview adapter
        binding.appointmentsRecyclerview.adapter = ViewAppointmentsAdapter(appointmentsList,  this)

        return binding.root
    }

    private fun observerList() {
        viewModel.appointmentsListLiveData.observe(viewLifecycleOwner) {
            appointmentsList.clear()
            if (it != null) {
                appointmentsList.addAll(it)

                binding.appointmentsRecyclerview.adapter?.notifyDataSetChanged()

                binding.noAppointmentAvailableTextview.visibility = View.GONE
                binding.appointmentsAvailableLayout.visibility = View.VISIBLE
            } else {
                binding.appointmentsAvailableLayout.visibility = View.GONE
                binding.noAppointmentAvailableTextview.visibility = View.VISIBLE
            }
            binding.progressbar.visibility = View.GONE
        }
    }

    override fun onViewButtonClick(currentAppointment: AppointmentModel) {
        val builder = AlertDialog.Builder(requireContext())
        val dialogBinding = DialogViewAppointmentBinding.inflate(layoutInflater)

        builder.setView(dialogBinding.root)
        builder.setCancelable(true)

        val alertDialog = builder.create()

        //make transparent to default dialog
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(0))

        //set details
        dialogBinding.lecturerTextview.text = currentAppointment.lecturer
        dialogBinding.dateTextview.text = currentAppointment.date
        dialogBinding.timeTextview.text = currentAppointment.time
        dialogBinding.appointmentStatusTextview.text = currentAppointment.appointment_status
        dialogBinding.rejectionReasonTextview.text = currentAppointment.rejection_reason

        if (currentAppointment.appointment_status == "rejected") {
            dialogBinding.rejectionSection.visibility = View.VISIBLE
        } else {
            dialogBinding.rejectionSection.visibility = View.GONE
        }


        if (currentAppointment.appointment_status == "pending") {
            dialogBinding.editButton.visibility = View.VISIBLE
        } else {
            dialogBinding.editButton.visibility = View.GONE
        }

        //Edit button click event
        dialogBinding.editButton.setOnClickListener {
            val bundle = bundleOf(
                "APPOINTMENT_ID" to currentAppointment.appointment_id,
                "USER_ID" to currentAppointment.user_id,
                "NAME" to currentAppointment.name,
                "MATRIC_NUMBER" to currentAppointment.matric_number,
                "EMAIL" to currentAppointment.email,
                "FACULTY" to currentAppointment.faculty,
                "LECTURER" to currentAppointment.lecturer,
                "DATE" to currentAppointment.date,
                "TIME" to currentAppointment.time,
                "APPOINTMENT_STATUS" to currentAppointment.appointment_status,
                "REJECTION_REASON" to currentAppointment.rejection_reason
            )
            findNavController().navigate(R.id.action_viewAppointmentsFragment_to_editAppointmentFragment, bundle)
            alertDialog.dismiss()
        }

        alertDialog.show()
    }
}



class ViewAppointmentsViewModelFactory(private val repository: ViewAppointmentsRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = ViewAppointmentsViewModel(repository) as T
}