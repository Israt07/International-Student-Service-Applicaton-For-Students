package com.company.iss.view.servicesActivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.company.iss.databinding.FragmentTranslatorBinding
import com.company.iss.model.TranslatorLanguageModel
import com.company.iss.utils.LoadingDialog
import com.company.iss.utils.showErrorToast
import com.company.iss.utils.showWarningToast
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import java.util.Locale

class TranslatorFragment : Fragment() {

    //Declaring variables
    private lateinit var binding: FragmentTranslatorBinding

    private var languageList: ArrayList<TranslatorLanguageModel>? = null

    private var sourceLanguageCode = "en"
    private var sourceLanguageTitle = "English"
    private var targetLanguageCode = "ur"
    private var targetLanguageTitle = "Urdu"

    private lateinit var translatorOptions: TranslatorOptions
    private lateinit var translator: Translator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTranslatorBinding.inflate(inflater, container, false)
        LoadingDialog.init(requireContext())

        loadAvailableLanguage()

        //back icon click event
        binding.backIcon.setOnClickListener { findNavController().popBackStack() }

        //source language button click event
        binding.sourceLanguageButton.setOnClickListener { sourceLanguageChoose() }

        //target language button click event
        binding.targetLanguageButton.setOnClickListener { targetLanguageChoose() }

        //translate button click event
        binding.translateButton.setOnClickListener { translate() }

        return binding.root
    }

    private fun loadAvailableLanguage() {
        languageList = ArrayList()

        val languageCodeList = TranslateLanguage.getAllLanguages()

        for (languageCode in languageCodeList) {
            val languageTitle = Locale(languageCode).displayLanguage
            languageList!!.add(TranslatorLanguageModel(languageCode, languageTitle))
        }
    }

    private fun sourceLanguageChoose() {
        val popupMenu = PopupMenu(requireContext(), binding.sourceLanguageButton)
        for (i in languageList!!.indices) {
            popupMenu.menu.add(Menu.NONE, i, i, languageList!![i].languageTitle)
        }

        popupMenu.show()

        popupMenu.setOnMenuItemClickListener { menuItem ->
            val position = menuItem.itemId

            sourceLanguageCode = languageList!![position].languageCode
            sourceLanguageTitle = languageList!![position].languageTitle

            binding.sourceLanguageButton.text = sourceLanguageTitle
            binding.sourceTextInputLayout.hint = "Enter $sourceLanguageTitle"

            false
        }
    }

    private fun targetLanguageChoose() {
        val popupMenu = PopupMenu(requireContext(), binding.targetLanguageButton)
        for (i in languageList!!.indices) {
            popupMenu.menu.add(Menu.NONE, i, i, languageList!![i].languageTitle)
        }

        popupMenu.show()

        popupMenu.setOnMenuItemClickListener { menuItem ->
            val position = menuItem.itemId

            targetLanguageCode = languageList!![position].languageCode
            targetLanguageTitle = languageList!![position].languageTitle

            binding.targetLanguageButton.text = targetLanguageTitle
            binding.targetTextInputLayout.hint = "$targetLanguageTitle Text"

            false
        }
    }

    private fun translate() {
        binding.translatedTextEdittext.setText("")

        if (binding.sourceTextEdittext.text.toString().trim().isEmpty()) {
            requireContext().showWarningToast("Enter text")
            binding.sourceTextEdittext.requestFocus()
            return
        }

        LoadingDialog.setText("Processing...")
        LoadingDialog.show()

        translatorOptions = TranslatorOptions.Builder()
            .setSourceLanguage(sourceLanguageCode)
            .setTargetLanguage(targetLanguageCode)
            .build()

        translator = Translation.getClient(translatorOptions)

        val downloadConditions = DownloadConditions.Builder()
            .requireWifi()
            .build()

        translator.downloadModelIfNeeded(downloadConditions)
            .addOnSuccessListener {
                translator.translate(binding.sourceTextEdittext.text.toString().trim())
                    .addOnSuccessListener { translatedText ->
                        LoadingDialog.dismiss()
                        binding.translatedTextEdittext.setText(translatedText)
                    }
                    .addOnFailureListener { e->
                        LoadingDialog.dismiss()
                        requireContext().showErrorToast("Failed due to ${e.message}")
                    }
            }
            .addOnFailureListener { e->
                LoadingDialog.dismiss()
                requireContext().showErrorToast("Failed due to ${e.message}")
            }
    }
}