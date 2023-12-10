package com.company.iss.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.company.iss.adapter.MapAdapter
import com.company.iss.databinding.FragmentMapBinding
import com.company.iss.interfaces.MapItemClickListener
import com.company.iss.model.MapModel
import com.company.iss.repository.MapRepository
import com.company.iss.utils.showErrorToast
import com.company.iss.view_model.MapViewModel

class MapFragment : Fragment(), MapItemClickListener {

    //declaring variables
    private lateinit var binding: FragmentMapBinding

    private lateinit var repository: MapRepository
    private lateinit var viewModel: MapViewModel

    private var mapList = ArrayList<MapModel>()
    private lateinit var mapAdapter: MapAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMapBinding.inflate(inflater, container, false)

        repository = MapRepository()
        viewModel = ViewModelProvider(this, MapViewModelFactory(repository))[MapViewModel::class.java]

        //request for data
        viewModel.requestMapList()

        observerList()

        //back button click event
        binding.backIcon.setOnClickListener { findNavController().popBackStack() }

        //set recyclerview adapter
        mapAdapter = MapAdapter(mapList,  this)
        binding.mapRecyclerview.adapter = mapAdapter

        //search
        binding.placeEdittext.doOnTextChanged { text, _, _, _ ->
            if (text.toString() == "") {
                mapAdapter.submitList(mapList)
            } else {
                searchMap(text.toString())
            }
        }

        return binding.root
    }

    private fun observerList() {
        viewModel.mapListLiveData.observe(viewLifecycleOwner) {
            mapList.clear()
            if (it != null) {
                mapList.addAll(it)

                binding.mapRecyclerview.adapter?.notifyDataSetChanged()

                binding.noMapAvailableTextview.visibility = View.GONE
                binding.placeTextInputLayout.visibility = View.VISIBLE
                binding.mapRecyclerview.visibility = View.VISIBLE
            } else {
                binding.placeTextInputLayout.visibility = View.GONE
                binding.mapRecyclerview.visibility = View.GONE
                binding.noMapAvailableTextview.visibility = View.VISIBLE
            }
            binding.progressbar.visibility = View.GONE
        }
    }

    private fun searchMap(query: String) {
        val filteredMapList = mapList.filter { it.map_title?.contains(query, ignoreCase = true) == true }
        mapAdapter.submitList(ArrayList(filteredMapList))
    }

    private fun openMap(url: String?) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        } catch (e: Exception) {
            requireContext().showErrorToast("Link not valid")
        }
    }

    override fun onMapButtonClick(currentMap: MapModel) {
        openMap(currentMap.map_link)
    }
}



class MapViewModelFactory(private val repository: MapRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = MapViewModel(repository) as T
}