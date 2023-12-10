package com.company.iss.view.servicesActivity.healthCareActivity.reportsActivity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.company.iss.R
import com.company.iss.databinding.FragmentReportsDetailsBinding
import com.company.iss.model.DoctorReportModel
import com.google.gson.Gson

class ReportsDetailsFragment : Fragment() {

    private lateinit var binding: FragmentReportsDetailsBinding

    private lateinit var reports: DoctorReportModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentReportsDetailsBinding.inflate(inflater, container, false)

        reports = Gson().fromJson(arguments?.getString("DATA"), DoctorReportModel::class.java)

        binding.doctorNameTextview.text = reports.doctor_name
        binding.dateOfReportTextview.text = reports.date_of_report
        binding.medicalHistoryTextview.text = reports.medical_history
        binding.currentSymptomsTextview.text = reports.current_symptoms
        binding.medicationNameTextview.text = reports.medication_name
        binding.dosageTextview.text = reports.dosage
        binding.frequencyTextview.text = reports.frequency
        binding.testNameTextview.text = reports.test_name
        binding.resultsTextview.text = reports.results
        binding.diagnosisTextview.text = reports.diagnosis
        binding.treatmentTextview.text = reports.prescription
        binding.doctorsNoteTextview.text = reports.doctors_note

        binding.backIcon.setOnClickListener { findNavController().popBackStack() }

        return binding.root
    }
}