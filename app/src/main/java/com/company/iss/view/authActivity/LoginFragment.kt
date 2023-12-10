package com.company.iss.view.authActivity

import android.app.AlertDialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.company.iss.R
import com.company.iss.databinding.DialogForgetPasswordBinding
import com.company.iss.databinding.FragmentLoginBinding
import com.company.iss.utils.KeyboardManager
import com.company.iss.utils.LoadingDialog
import com.company.iss.utils.NetworkManager
import com.company.iss.utils.SharedPref
import com.company.iss.utils.showErrorToast
import com.company.iss.utils.showSuccessToast
import com.company.iss.utils.showWarningToast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException


class LoginFragment : Fragment() {

    //Declaring variables
    private lateinit var binding: FragmentLoginBinding

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        LoadingDialog.init(requireContext())
        SharedPref.init(requireContext())

        firebaseAuth = FirebaseAuth.getInstance()

        //back button click event
        binding.backIcon.setOnClickListener { findNavController().popBackStack() }

        //register text click event
        binding.registerButton.setOnClickListener { findNavController().navigate(R.id.action_loginFragment_to_registerFragment) }

        //Login button click event
        binding.loginButton.setOnClickListener { logInUser(it) }

        //forget password text click event
        binding.forgetPasswordButton.setOnClickListener { forgetPasswordDialog() }

        return binding.root
    }

    private fun forgetPasswordDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val dialogBinding = DialogForgetPasswordBinding.inflate(layoutInflater)

        builder.setView(dialogBinding.root)
        builder.setCancelable(true)

        val alertDialog = builder.create()

        //make transparent to default dialog
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(0))

        //submit button click event
        dialogBinding.submitButton.setOnClickListener { forgetPassword(dialogBinding, alertDialog, it) }

        alertDialog.show()
    }

    private fun forgetPassword(dialogBinding: DialogForgetPasswordBinding, alertDialog: AlertDialog, view: View) {
        if (dialogBinding.emailEdittext.text.toString().trim().isEmpty()) {
            dialogBinding.emailEdittext.error = "Enter your email"
            dialogBinding.emailEdittext.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(dialogBinding.emailEdittext.text.toString().trim()).matches()) {
            dialogBinding.emailEdittext.error = "Enter valid email"
            dialogBinding.emailEdittext.requestFocus()
            return
        }

        if (NetworkManager.isInternetAvailable(requireContext())) {
            LoadingDialog.setText("Please wait...")
            LoadingDialog.show()

            firebaseAuth.sendPasswordResetEmail(dialogBinding.emailEdittext.text.toString().trim()).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    LoadingDialog.dismiss()
                    alertDialog.dismiss()
                    requireContext().showSuccessToast("Recovery link sent to email", Toast.LENGTH_LONG)
                    KeyboardManager.hideKeyBoard(requireContext(), view)
                } else {
                    LoadingDialog.dismiss()
                    try {
                        throw task.exception!!
                    }
                    catch (invalidEmail: FirebaseAuthInvalidUserException) {
                        dialogBinding.emailEdittext.error = "Email not registered"
                        dialogBinding.emailEdittext.requestFocus()
                    } catch (e: Exception) {
                        requireContext().showErrorToast(e.message.toString())
                    }
                }
            }
        } else {
            requireContext().showWarningToast("No internet available")
        }
    }

    private fun logInUser(view: View) {
        if (binding.emailEdittext.text.toString().trim().isEmpty()) {
            binding.emailEdittext.error = "Enter your email"
            binding.emailEdittext.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(binding.emailEdittext.text.toString().trim()).matches()) {
            binding.emailEdittext.error = "Enter valid email"
            binding.emailEdittext.requestFocus()
            return
        }
        if (binding.passwordEdittext.text.toString().isEmpty()) {
            binding.passwordEdittext.error = "Enter password"
            binding.passwordEdittext.requestFocus()
            return
        }
        if (binding.passwordEdittext.text.toString().trim().length < 6) {
            binding.passwordEdittext.error = "Must be 6 character"
            binding.passwordEdittext.requestFocus()
            return
        }

        if (NetworkManager.isInternetAvailable(requireContext())) {
            LoadingDialog.setText("Signing in...")
            LoadingDialog.show()

            firebaseAuth.signInWithEmailAndPassword(binding.emailEdittext.text.toString().trim(), binding.passwordEdittext.text.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        requireContext().showSuccessToast("Successful")
                        LoadingDialog.dismiss()
                        KeyboardManager.hideKeyBoard(requireContext(), view)
                        findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                    } else {
                        LoadingDialog.dismiss()
                        try {
                            throw task.exception!!
                        }
                        catch (invalidEmail: FirebaseAuthInvalidUserException) {
                            requireContext().showErrorToast("Email not registered")
                        }
                        catch (wrongPassword: FirebaseAuthInvalidCredentialsException) {
                            requireContext().showErrorToast("Wrong password")
                        } catch (e: Exception) {
                            requireContext().showErrorToast(e.message.toString())
                        }
                    }
                }
        } else {
            requireContext().showWarningToast("No internet available")
        }
    }
}