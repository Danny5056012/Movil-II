package com.example.autodrivemanagermvvm.ui.fragment

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.autodrivemanagermvvm.R
import com.example.autodrivemanagermvvm.data.repository.CarRepository
import com.example.autodrivemanagermvvm.databinding.FragmentRegisterBinding
import com.example.autodrivemanagermvvm.ui.common.UiState
import com.example.autodrivemanagermvvm.ui.viewmodel.RegisterViewModel
import com.example.autodrivemanagermvvm.ui.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: RegisterViewModel
    private var registerRequested = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val factory = ViewModelFactory(CarRepository())
        viewModel = ViewModelProvider(this, factory)[RegisterViewModel::class.java]

        binding.btnRegister.setOnClickListener {
            val first = binding.etFirstName.text?.toString().orEmpty()
            val last = binding.etLastName.text?.toString().orEmpty()
            val email = binding.etEmail.text?.toString().orEmpty()
            val pass = binding.etPassword.text?.toString().orEmpty()

            if (first.isBlank() || last.isBlank() || email.isBlank() || pass.isBlank()) {
                Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(requireContext(), "Email inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            registerRequested = true
            viewModel.register(first, last, email, pass)
        }

        binding.btnGoToLogin.setOnClickListener {
            findNavController().popBackStack()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is UiState.Loading -> {
                        binding.progress.visibility = View.VISIBLE
                        binding.btnRegister.isEnabled = false
                    }

                    is UiState.Success -> {
                        binding.progress.visibility = View.GONE
                        binding.btnRegister.isEnabled = true
                        if (registerRequested) {
                            registerRequested = false
                            Toast.makeText(requireContext(), "Registro exitoso. Inicia sesión.", Toast.LENGTH_SHORT).show()
                            findNavController().popBackStack()
                        }
                    }

                    is UiState.Error -> {
                        registerRequested = false
                        binding.progress.visibility = View.GONE
                        binding.btnRegister.isEnabled = true
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

