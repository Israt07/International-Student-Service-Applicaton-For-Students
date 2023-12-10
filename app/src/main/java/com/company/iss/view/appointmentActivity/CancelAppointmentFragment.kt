package com.company.iss.view.appointmentActivity

import android.app.AlertDialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.company.iss.adapter.CancelAppointmentAdapter
import com.company.iss.databinding.DialogCancelAppointmentBinding
import com.company.iss.databinding.FragmentCancelAppointmentBinding
import com.company.iss.interfaces.CancelAppointmentItemClickListener
import com.company.iss.model.AppointmentModel
import com.company.iss.repository.CancelAppointmentRepository
import com.company.iss.utils.LoadingDialog
import com.company.iss.utils.NetworkManager
import com.company.iss.utils.showErrorToast
import com.company.iss.utils.showSuccessToast
import com.company.iss.utils.showWarningToast
import com.company.iss.view_model.CancelAppointmentViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class CancelAppointmentFragment : Fragment(), CancelAppointmentItemClickListener {

    //Declaring variables
    private lateinit var binding: FragmentCancelAppointmentBinding

    private lateinit var repository: CancelAppointmentRepository
    private lateinit var viewModel: CancelAppointmentViewModel

    private var pendingAppointmentsList = ArrayList<AppointmentModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCancelAppointmentBinding.inflate(inflater, container, false)
        LoadingDialog.init(requireContext())

        repository = CancelAppointmentRepository()
        viewModel = ViewModelProvider(this, CancelAppointmentViewModelFactory(repository))[CancelAppointmentViewModel::class.java]

        //request for data
        viewModel.requestPendingAppointmentsList(FirebaseAuth.getInstance().currentUser!!.uid)

        observerList()

        //back button click event
        binding.backIcon.setOnClickListener { findNavController().popBackStack() }

        //set details
        binding.appointmentsRecyclerview.adapter = CancelAppointmentAdapter(pendingAppointmentsList,  this@CancelAppointmentFragment)

        return binding.root
    }

    private fun observerList() {
        viewModel.pendingAppointmentListLiveData.observe(viewLifecycleOwner) {
            pendingAppointmentsList.clear()
            if (it != null) {
                it.forEach {appointmentModel ->
                    if (appointmentModel.appointment_status == "pending") {
                        pendingAppointmentsList.add(appointmentModel)
                    }
                }

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

    override fun onCancelButtonClick(currentAppointment: AppointmentModel) {
        val builder = AlertDialog.Builder(requireContext())
        val dialogBinding = DialogCancelAppointmentBinding.inflate(layoutInflater)

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

        //cancel click event
        dialogBinding.cancelButton.setOnClickListener { cancelAppointment(currentAppointment.appointment_id.toString(), alertDialog) }

        alertDialog.show()
    }

    private fun cancelAppointment(appointmentId: String, alertDialog: AlertDialog) {
        if (NetworkManager.isInternetAvailable(requireContext())) {
            LoadingDialog.setText("Cancelling...")
            LoadingDialog.show()

            Firebase.database.reference.child("appointments").child(appointmentId).removeValue()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        requireContext().showSuccessToast("Booking Cancelled...")
                        alertDialog.dismiss()
                    } else {
                        requireContext().showErrorToast("Something Wrong")
                    }
                    LoadingDialog.dismiss()
                }
        } else {
            requireContext().showWarningToast("No internet available")
        }
    }
}



class CancelAppointmentViewModelFactory(private val repository: CancelAppointmentRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = CancelAppointmentViewModel(repository) as T
}