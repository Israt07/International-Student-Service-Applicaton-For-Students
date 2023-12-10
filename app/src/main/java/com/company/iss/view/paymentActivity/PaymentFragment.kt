package com.company.iss.view.paymentActivity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.company.iss.R
import com.company.iss.adapter.DoctorAdapter
import com.company.iss.adapter.PaymentAdapter
import com.company.iss.databinding.FragmentPaymentBinding
import com.company.iss.model.PaymentModel
import com.company.iss.model.UserModel
import com.company.iss.repository.PaymentRepository
import com.company.iss.repository.ProfileRepository
import com.company.iss.utils.SharedPref
import com.company.iss.utils.loadImage
import com.company.iss.view.profileActivity.ProfileViewModelFactory
import com.company.iss.view_model.PaymentViewModel
import com.company.iss.view_model.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class PaymentFragment : Fragment() {

    private lateinit var binding: FragmentPaymentBinding

    private lateinit var repository: PaymentRepository
    private lateinit var viewModel: PaymentViewModel

    private var paymentList = ArrayList<PaymentModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPaymentBinding.inflate(inflater, container, false)

        repository = PaymentRepository()
        viewModel = ViewModelProvider(this, PaymentViewModelFactory(repository))[PaymentViewModel::class.java]

        //request for data
        viewModel.requestPaymentDetails(FirebaseAuth.getInstance().currentUser!!.uid)

        observerList()

        //back button click event
        binding.backIcon.setOnClickListener { findNavController().popBackStack() }

        //set recyclerview adapter
        binding.paymentListRecyclerview.adapter = PaymentAdapter(paymentList)

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun observerList() {
        viewModel.paymentDetailsLiveData.observe(viewLifecycleOwner) { paymentModels ->
            paymentList.clear()
            if (paymentModels.isNullOrEmpty()) {
                val paymentModel1 = PaymentModel("1", FirebaseAuth.getInstance().currentUser!!.uid, "", "3500", "3500", "09/06/2023", "Due")

                Firebase.database.reference.child("payments").child(FirebaseAuth.getInstance().currentUser!!.uid).child("1")
                    .setValue(paymentModel1).addOnCompleteListener { task1 ->
                        val paymentModel2 = PaymentModel("2", FirebaseAuth.getInstance().currentUser!!.uid, "", "420", "420", "09/06/2023", "Due")

                        Firebase.database.reference.child("payments").child(FirebaseAuth.getInstance().currentUser!!.uid).child("2")
                            .setValue(paymentModel2).addOnCompleteListener { task2 ->
                                val paymentModel3 = PaymentModel("3", FirebaseAuth.getInstance().currentUser!!.uid, "", "75", "75", "09/06/2023", "Due")

                                Firebase.database.reference.child("payments").child(FirebaseAuth.getInstance().currentUser!!.uid).child("3")
                                    .setValue(paymentModel3).addOnCompleteListener { task3 ->

                                    }
                            }
                    }
            } else {
                paymentList.addAll(paymentModels)
                binding.paymentListRecyclerview.adapter?.notifyDataSetChanged()

                binding.totalTextview.text = "Total: ${paymentModels.filter { it.debit_rm != null }.sumByDouble { it.debit_rm!!.toDouble() }.toInt()}"

                binding.dateTextview.text = "Date: ${paymentModels[0].credit_date}"

                binding.progressbar.visibility = View.GONE
            }
        }
    }
}




class PaymentViewModelFactory(private val repository: PaymentRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = PaymentViewModel(repository) as T
}