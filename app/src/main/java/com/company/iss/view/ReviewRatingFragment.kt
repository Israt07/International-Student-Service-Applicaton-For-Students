package com.company.iss.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.company.iss.databinding.FragmentReviewRatingBinding
import com.company.iss.model.ReviewRatingModel
import com.company.iss.utils.KeyboardManager
import com.company.iss.utils.LoadingDialog
import com.company.iss.utils.NetworkManager
import com.company.iss.utils.SharedPref
import com.company.iss.utils.showErrorToast
import com.company.iss.utils.showSuccessToast
import com.company.iss.utils.showWarningToast
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReviewRatingFragment : Fragment() {

    //Declaring variables
    private lateinit var binding: FragmentReviewRatingBinding

    private var selectedRating = "0.0"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentReviewRatingBinding.inflate(inflater, container, false)
        LoadingDialog.init(requireContext())
        SharedPref.init(requireContext())

        //back button click event
        binding.backIcon.setOnClickListener { findNavController().popBackStack() }

        //Rating bar seeking event
        binding.ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
            selectedRating = rating.toString()
        }

        //submit button click event
        binding.submitButton.setOnClickListener {
            if (binding.reviewEdittext.text.toString().trim().isEmpty() or (selectedRating == "0.0")) {
                requireContext().showWarningToast("Please write your review")
                return@setOnClickListener
            }

            addReview(it)
        }

        return binding.root
    }

    private fun addReview(view: View) {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy    hh:mm:ss a", Locale.getDefault())
        val date = Date()
        val dateWithTime = dateFormat.format(date)

        val reviewModel = ReviewRatingModel(SharedPref.read("USER_ID", ""), SharedPref.read("USER_NAME", ""), dateWithTime, selectedRating, binding.reviewEdittext.text.toString().trim())

        if (NetworkManager.isInternetAvailable(requireContext())) {
            KeyboardManager.hideKeyBoard(requireContext(), view)
            LoadingDialog.setText("Adding Review...")
            LoadingDialog.show()

            Firebase.database.reference.child("review_and_rating").child(SharedPref.read("USER_ID", "").toString()).setValue(reviewModel).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    requireContext().showSuccessToast("Successful")
                    LoadingDialog.dismiss()
                    findNavController().popBackStack()
                } else {
                    requireContext().showErrorToast(task.exception?.localizedMessage.toString())
                    LoadingDialog.dismiss()
                }
            }.addOnFailureListener { e ->
                requireContext().showErrorToast(e.localizedMessage.toString())
                LoadingDialog.dismiss()
            }
        } else {
            requireContext().showWarningToast("No internet available")
        }
    }
}