package com.example.autodrivemanagermvvm.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.autodrivemanagermvvm.data.repository.CarRepository
import com.example.autodrivemanagermvvm.databinding.FragmentCarDetailBinding
import com.example.autodrivemanagermvvm.ui.common.UiState
import com.example.autodrivemanagermvvm.ui.viewmodel.CarDetailViewModel
import com.example.autodrivemanagermvvm.ui.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch
import java.util.Locale

class CarDetailFragment : Fragment() {
    private var _binding: FragmentCarDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: CarDetailViewModel
    private var carId: Int = -1
    private var deleteRequested = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        carId = arguments?.getInt("carId", -1) ?: -1
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCarDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val factory = ViewModelFactory(CarRepository())
        viewModel = ViewModelProvider(this, factory)[CarDetailViewModel::class.java]

        if (carId <= 0) {
            Toast.makeText(requireContext(), "ID de vehículo inválido", Toast.LENGTH_LONG).show()
            findNavController().popBackStack()
            return
        }

        binding.btnDelete.setOnClickListener {
            deleteRequested = true
            viewModel.deleteCar(carId)
        }

        viewModel.loadCarDetail(carId)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is UiState.Loading -> binding.progress.visibility = View.VISIBLE
                    is UiState.Error -> {
                        binding.progress.visibility = View.GONE
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                    }

                    is UiState.Success -> {
                        binding.progress.visibility = View.GONE
                        val car = state.data
                        binding.ivCar.setImageResource(car.imageResId)
                        binding.tvTitle.text = "${car.make} ${car.model}"
                        binding.tvYear.text = "Año: ${car.year}"
                        binding.tvSpeedKmh.text = "Velocidad: ${car.speed} km/h"

                        val mph = viewModel.getSpeedInMph(car.speed)
                        binding.tvSpeedMph.text = "Velocidad: ${String.format(Locale.US, "%.2f", mph)} mph"
                        binding.tvFuel.text = "Combustible: ${car.fuel}"
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.deleteState.collect { state ->
                when (state) {
                    is UiState.Loading -> {
                        binding.progress.visibility = View.VISIBLE
                        binding.btnDelete.isEnabled = false
                    }

                    is UiState.Success -> {
                        binding.progress.visibility = View.GONE
                        binding.btnDelete.isEnabled = true
                        if (deleteRequested) {
                            deleteRequested = false
                            Toast.makeText(requireContext(), "Vehículo eliminado", Toast.LENGTH_SHORT).show()
                            findNavController().popBackStack()
                        }
                    }

                    is UiState.Error -> {
                        binding.progress.visibility = View.GONE
                        binding.btnDelete.isEnabled = true
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

