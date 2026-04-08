package com.example.autodrivemanagermvvm.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.lifecycle.ViewModelProvider
import com.example.autodrivemanagermvvm.R
import com.example.autodrivemanagermvvm.data.repository.CarRepository
import com.example.autodrivemanagermvvm.databinding.FragmentLoginBinding
import com.example.autodrivemanagermvvm.ui.common.UiState
import com.example.autodrivemanagermvvm.ui.viewmodel.LoginViewModel
import com.example.autodrivemanagermvvm.ui.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: LoginViewModel
    private var loginRequested = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val factory = ViewModelFactory(CarRepository())
        viewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]

        binding.btnLogin.setOnClickListener {
            val user = binding.etUser.text?.toString().orEmpty()
            val pass = binding.etPass.text?.toString().orEmpty()
            if (user.isBlank() || pass.isBlank()) {
                Toast.makeText(requireContext(), "Completa email y contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            loginRequested = true
            viewModel.login(user, pass)
        }

        binding.btnGoToRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is UiState.Loading -> {
                        binding.progress.visibility = View.VISIBLE
                        binding.btnLogin.isEnabled = false
                    }

                    is UiState.Success -> {
                        binding.progress.visibility = View.GONE
                        binding.btnLogin.isEnabled = true
                        if (loginRequested) {
                            loginRequested = false
                            findNavController().navigate(R.id.action_loginFragment_to_carListFragment)
                        }
                    }

                    is UiState.Error -> {
                        loginRequested = false
                        binding.progress.visibility = View.GONE
                        binding.btnLogin.isEnabled = true
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

