package com.company.iss.view.servicesActivity.housingActivity

import android.app.AlertDialog
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
import com.company.iss.adapter.HousingImageAdapter
import com.company.iss.databinding.DialogHousingBookBinding
import com.company.iss.databinding.FragmentHousingDetailsBinding
import com.company.iss.model.HousingBookingModel
import com.company.iss.model.HousingImageModel
import com.company.iss.repository.HousingDetailsRepository
import com.company.iss.utils.KeyboardManager
import com.company.iss.utils.LoadingDialog
import com.company.iss.utils.NetworkManager
import com.company.iss.utils.SharedPref
import com.company.iss.utils.showErrorToast
import com.company.iss.utils.showSuccessToast
import com.company.iss.utils.showWarningToast
import com.company.iss.view_model.HousingDetailsViewModel
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class HousingDetailsFragment : Fragment() {

    //Declaring variables
    private lateinit var binding: FragmentHousingDetailsBinding

    private lateinit var repository: HousingDetailsRepository
    private lateinit var viewModel: HousingDetailsViewModel

    private var title = ""
    private var housingImageList = ArrayList<HousingImageModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHousingDetailsBinding.inflate(inflater, container, false)
        SharedPref.init(requireContext())
        LoadingDialog.init(requireContext())

        title = arguments?.getString("TITLE").toString()

        repository = HousingDetailsRepository()
        viewModel = ViewModelProvider(this, HousingDetailsViewModelFactory(repository))[HousingDetailsViewModel::class.java]

        //request for data
        viewModel.requestHousingImages(title)

        observerList()

        //back button click event
        binding.backIcon.setOnClickListener { findNavController().popBackStack() }

        //set title text
        binding.titleTextview.text = title

        //set recyclerview adapter
        binding.imageRecyclerview.adapter = HousingImageAdapter(housingImageList)

        //book now button click event
        binding.bookNowButton.setOnClickListener { bookNowDialog() }

        return binding.root
    }

    private fun observerList() {
        viewModel.housingImagesLiveData.observe(viewLifecycleOwner) {
            housingImageList.clear()
            if (it != null) {
                housingImageList.addAll(it)

                housingImageList.reverse()

                binding.imageRecyclerview.adapter?.notifyDataSetChanged()
            }
            //request for data
            viewModel.requestHousingDetails(title)
        }

        viewModel.housingDetailsLiveData.observe(viewLifecycleOwner) {
            it?.let {
                binding.detailsTextview.text = it.details
                binding.bookNowButton.visibility = View.VISIBLE
            }
        }
    }

    private fun bookNowDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val dialogBinding = DialogHousingBookBinding.inflate(layoutInflater)

        builder.setView(dialogBinding.root)
        builder.setCancelable(true)

        val alertDialog = builder.create()

        //make transparent to default dialog
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(0))

        //set details
        val durationOfStayList = arrayOf("1 Month", "2 Months", "3 Months", "4 Months", "5 Months", "6 Months")
        var selectedDurationOfStay = ""
        val durationOfStayItemAdapter = ArrayAdapter(requireContext(), R.layout.single_dropdown_item, durationOfStayList)
        dialogBinding.durationOfStaySelectAutoCompleteTextview.setAdapter(durationOfStayItemAdapter)
        dialogBinding.durationOfStaySelectAutoCompleteTextview.setOnClickListener {
            dialogBinding.durationOfStaySelectAutoCompleteTextview.setText("")
        }
        dialogBinding.durationOfStaySelectAutoCompleteTextview.doOnTextChanged { text, _, _, _ ->
            selectedDurationOfStay = text.toString()
        }

        val yearList = arrayOf("1", "2", "3")
        var selectedYear = ""
        val yearItemAdapter = ArrayAdapter(requireContext(), R.layout.single_dropdown_item, yearList)
        dialogBinding.yearSelectAutoCompleteTextview.setAdapter(yearItemAdapter)
        dialogBinding.yearSelectAutoCompleteTextview.setOnClickListener {
            dialogBinding.yearSelectAutoCompleteTextview.setText("")
        }
        dialogBinding.yearSelectAutoCompleteTextview.doOnTextChanged { text, _, _, _ ->
            selectedYear = text.toString()
        }

        val raceList = arrayOf("Malay", "Chinese", "Indian", "International")
        var selectedRace = ""
        val raceItemAdapter = ArrayAdapter(requireContext(), R.layout.single_dropdown_item, raceList)
        dialogBinding.raceSelectAutoCompleteTextview.setAdapter(raceItemAdapter)
        dialogBinding.raceSelectAutoCompleteTextview.setOnClickListener {
            dialogBinding.raceSelectAutoCompleteTextview.setText("")
        }
        dialogBinding.raceSelectAutoCompleteTextview.doOnTextChanged { text, _, _, _ ->
            selectedRace = text.toString()
        }

        //book now button click event
        dialogBinding.bookNowButton.setOnClickListener {
            if (selectedDurationOfStay.isEmpty()) {
                requireContext().showWarningToast("Select duration of stay")
                return@setOnClickListener
            }
            if (selectedYear.isEmpty()) {
                requireContext().showWarningToast("Select year")
                return@setOnClickListener
            }
            if (!yearList.contains(selectedYear)) {
                requireContext().showWarningToast("Select valid year")
                return@setOnClickListener
            }
            if (selectedRace.isEmpty()) {
                requireContext().showWarningToast("Select race")
                return@setOnClickListener
            }
            if (!raceList.contains(selectedRace)) {
                requireContext().showWarningToast("Select valid race")
                return@setOnClickListener
            }

            if (NetworkManager.isInternetAvailable(requireContext())) {
                LoadingDialog.setText("Booking Now...")
                LoadingDialog.show()
                KeyboardManager.hideKeyBoard(requireContext(), it)

                val bookingId = Firebase.database.reference.child("services").child("housing").child("bookings").push().key.toString()

                val housingBookingModel = HousingBookingModel(bookingId, SharedPref.read("USER_ID", ""), SharedPref.read("USER_NAME", ""), SharedPref.read("USER_GENDER", ""), selectedRace, SharedPref.read("USER_MATRIC_NUMBER", ""), SharedPref.read("USER_EMAIL", ""), SharedPref.read("USER_FACULTY", ""), SharedPref.read("USER_COURSE", ""), selectedYear, title, selectedDurationOfStay, "pending", "pending", "")

                Firebase.database.reference.child("services").child("housing").child("bookings").child(bookingId)
                    .setValue(housingBookingModel).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            requireContext().showSuccessToast("Booking successful")
                            alertDialog.dismiss()
                        } else {
                            requireContext().showErrorToast("Something wrong.")
                        }
                        LoadingDialog.dismiss()
                    }
            } else {
                requireContext().showWarningToast("No internet available")
            }
        }

        alertDialog.show()
    }
}



class HousingDetailsViewModelFactory(private val repository: HousingDetailsRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = HousingDetailsViewModel(repository) as T
}