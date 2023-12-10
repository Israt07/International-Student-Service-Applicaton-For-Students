package com.company.iss.view.servicesActivity.housingActivity

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
import com.company.iss.R
import com.company.iss.adapter.ViewBookingsAdapter
import com.company.iss.databinding.DialogViewBookingBinding
import com.company.iss.databinding.FragmentViewBookingHistoryBinding
import com.company.iss.interfaces.ViewBookingsItemClickListener
import com.company.iss.model.HousingBookingModel
import com.company.iss.repository.ViewBookingsRepository
import com.company.iss.utils.LoadingDialog
import com.company.iss.utils.NetworkManager
import com.company.iss.utils.showErrorToast
import com.company.iss.utils.showSuccessToast
import com.company.iss.view_model.ViewBookingsViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ViewBookingHistoryFragment : Fragment(), ViewBookingsItemClickListener {

    //Declaring variables
    private lateinit var binding: FragmentViewBookingHistoryBinding

    private lateinit var repository: ViewBookingsRepository
    private lateinit var viewModel: ViewBookingsViewModel

    private var bookingList = ArrayList<HousingBookingModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentViewBookingHistoryBinding.inflate(inflater, container, false)
        LoadingDialog.init(requireContext())

        repository = ViewBookingsRepository()
        viewModel = ViewModelProvider(this, ViewBookingHistoryViewModelFactory(repository))[ViewBookingsViewModel::class.java]

        //request for data
        viewModel.requestBookingList(FirebaseAuth.getInstance().currentUser!!.uid)

        observerList()

        //back button click event
        binding.backIcon.setOnClickListener { findNavController().popBackStack() }

        //set recyclerview adapter
        binding.bookingRecyclerview.adapter = ViewBookingsAdapter(bookingList,  this)

        return binding.root
    }

    private fun observerList() {
        viewModel.bookingListLiveData.observe(viewLifecycleOwner) {
            bookingList.clear()
            if (it != null) {
                bookingList.addAll(it)

                binding.bookingRecyclerview.adapter?.notifyDataSetChanged()

                binding.noBookingAvailableTextview.visibility = View.GONE
                binding.bookingAvailableLayout.visibility = View.VISIBLE
            } else {
                binding.bookingAvailableLayout.visibility = View.GONE
                binding.noBookingAvailableTextview.visibility = View.VISIBLE
            }
            binding.progressbar.visibility = View.GONE
        }
    }

    override fun onViewButtonClick(currentItem: HousingBookingModel) {
        val builder = AlertDialog.Builder(requireContext())
        val dialogBinding = DialogViewBookingBinding.inflate(layoutInflater)

        builder.setView(dialogBinding.root)
        builder.setCancelable(true)

        val alertDialog = builder.create()

        //make transparent to default dialog
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(0))

        //set details
        dialogBinding.housingTypeTextview.text = currentItem.housing_type
        dialogBinding.durationOfStayTextview.text = currentItem.duration_of_stay
        dialogBinding.paymentStatusTextview.text = currentItem.payment_status
        dialogBinding.bookingStatusTextview.text = currentItem.booking_status
        dialogBinding.noteTextview.text = currentItem.note

        if (currentItem.booking_status != "pending") {
            dialogBinding.noteSection.visibility = View.VISIBLE
        } else {
            dialogBinding.noteSection.visibility = View.GONE
        }


        if (currentItem.booking_status == "pending") {
            dialogBinding.cancelButton.visibility = View.VISIBLE
        } else {
            dialogBinding.cancelButton.visibility = View.GONE
        }

        //cancel button click event
        dialogBinding.cancelButton.setOnClickListener {
            cancelBooking(currentItem.booking_id.toString(), alertDialog)
        }

        alertDialog.show()
    }

    private fun cancelBooking(itemId: String, mainAlertDialog: AlertDialog) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Confirmation")
        builder.setIcon(R.mipmap.ic_launcher)
        builder.setMessage("Do You Really Want To Cancel This?")
        builder.setPositiveButton("Yes") {dialog, _ ->
            if (NetworkManager.isInternetAvailable(requireContext())){
                LoadingDialog.setText("Cancelling...")
                LoadingDialog.show()

                Firebase.database.reference.child("services").child("housing").child("bookings").child(itemId).removeValue()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            requireContext().showSuccessToast("Booking Cancelled...")
                            dialog.dismiss()
                            mainAlertDialog.dismiss()
                        } else {
                            requireContext().showErrorToast("Something Wrong")
                        }
                        LoadingDialog.dismiss()
                    }
            } else {
                requireContext().showErrorToast("No internet connection")
            }
        }
        builder.setNegativeButton("No") {dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog = builder.create()
        alertDialog.show()
    }
}



class ViewBookingHistoryViewModelFactory(private val repository: ViewBookingsRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = ViewBookingsViewModel(repository) as T
}