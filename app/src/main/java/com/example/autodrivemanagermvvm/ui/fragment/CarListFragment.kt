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
import com.example.autodrivemanagermvvm.R
import com.example.autodrivemanagermvvm.data.repository.CarRepository
import com.example.autodrivemanagermvvm.databinding.FragmentCarListBinding
import com.example.autodrivemanagermvvm.ui.adapter.CarListAdapter
import com.example.autodrivemanagermvvm.ui.common.UiState
import com.example.autodrivemanagermvvm.ui.viewmodel.CarListViewModel
import com.example.autodrivemanagermvvm.ui.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch

class CarListFragment : Fragment() {
    private var _binding: FragmentCarListBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: CarListViewModel
    private lateinit var adapter: CarListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCarListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val factory = ViewModelFactory(CarRepository())
        viewModel = ViewModelProvider(this, factory)[CarListViewModel::class.java]

        adapter = CarListAdapter { car ->
            findNavController().navigate(
                R.id.action_carListFragment_to_carDetailFragment,
                Bundle().apply { putInt("carId", car.id) }
            )
        }
        binding.rvCars.adapter = adapter

        binding.btnAdd.setOnClickListener {
            findNavController().navigate(R.id.action_carListFragment_to_carRegisterFragment)
        }

        binding.searchView.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = true
            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.onSearchQueryChanged(newText.orEmpty())
                return true
            }
        })

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is UiState.Loading -> {
                        binding.progress.visibility = View.VISIBLE
                        binding.tvEmpty.visibility = View.GONE
                        binding.rvCars.visibility = View.GONE
                    }

                    is UiState.Success -> {
                        binding.progress.visibility = View.GONE
                        val list = state.data
                        adapter.submitList(list)
                        val empty = list.isEmpty()
                        binding.tvEmpty.text = "Aún no tienes vehículos.\nRegistra el primero para empezar."
                        binding.tvEmpty.visibility = if (empty) View.VISIBLE else View.GONE
                        binding.rvCars.visibility = if (empty) View.GONE else View.VISIBLE
                    }

                    is UiState.Error -> {
                        binding.progress.visibility = View.GONE
                        binding.tvEmpty.visibility = View.VISIBLE
                        binding.rvCars.visibility = View.GONE
                        binding.tvEmpty.text = "Error: ${state.message}"
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

