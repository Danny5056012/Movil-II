package com.example.autodrivemanagermvvm.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.autodrivemanagermvvm.R
import com.example.autodrivemanagermvvm.data.repository.CarRepository
import com.example.autodrivemanagermvvm.databinding.FragmentCarRegisterBinding
import com.example.autodrivemanagermvvm.ui.common.UiState
import com.example.autodrivemanagermvvm.ui.viewmodel.CarRegisterViewModel
import com.example.autodrivemanagermvvm.ui.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch

class CarRegisterFragment : Fragment() {
    private var _binding: FragmentCarRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: CarRegisterViewModel
    private var saveRequested = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCarRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val factory = ViewModelFactory(CarRepository())
        viewModel = ViewModelProvider(this, factory)[CarRegisterViewModel::class.java]

        // Default: ensure at least one local icon is selected so the user can save.
        if (binding.chipGroupImages.checkedChipId == View.NO_ID) {
            binding.chipCar1.isChecked = true
            viewModel.selectImage(R.drawable.ic_car_1)
        }

        binding.etMake.addTextChangedListener(SimpleTextWatcher(viewModel::updateMake))
        binding.etModel.addTextChangedListener(SimpleTextWatcher(viewModel::updateModel))
        binding.etYear.addTextChangedListener(SimpleTextWatcher(viewModel::updateYear))
        binding.etSpeed.addTextChangedListener(SimpleTextWatcher(viewModel::updateSpeed))
        binding.etFuel.addTextChangedListener(SimpleTextWatcher(viewModel::updateFuel))

        binding.chipGroupImages.setOnCheckedStateChangeListener { _, checkedIds ->
            val imageRes = when (checkedIds.firstOrNull()) {
                R.id.chipCar1 -> R.drawable.ic_car_1
                R.id.chipCar2 -> R.drawable.ic_car_2
                R.id.chipCar3 -> R.drawable.ic_car_3
                R.id.chipCar4 -> R.drawable.ic_car_4
                R.id.chipCar5 -> R.drawable.ic_car_5
                else -> null
            }
            if (imageRes == null) viewModel.clearImageSelection() else viewModel.selectImage(imageRes)
        }

        binding.btnSave.setOnClickListener {
            saveRequested = true
            viewModel.saveCar()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isSaveEnabled.collect { enabled ->
                binding.btnSave.isEnabled = enabled
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.saveUiState.collect { state ->
                when (state) {
                    is UiState.Loading -> {
                        binding.progress.visibility = View.VISIBLE
                        binding.btnSave.isEnabled = false
                    }

                    is UiState.Success -> {
                        binding.progress.visibility = View.GONE
                        if (saveRequested) {
                            saveRequested = false
                            Toast.makeText(requireContext(), "Vehículo registrado", Toast.LENGTH_SHORT).show()
                            findNavController().navigate(R.id.action_carRegisterFragment_to_carListFragment)
                        }
                    }

                    is UiState.Error -> {
                        saveRequested = false
                        binding.progress.visibility = View.GONE
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

