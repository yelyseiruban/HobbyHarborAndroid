package com.yelysei.hobbyharbor.ui.screens.main.categorizedhobbies

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.yelysei.hobbyharbor.R
import com.yelysei.hobbyharbor.Repositories.hobbiesRepository
import com.yelysei.hobbyharbor.Repositories.userHobbiesRepository
import com.yelysei.hobbyharbor.databinding.FragmentCategorizedHobbiesBinding
import com.yelysei.hobbyharbor.model.hobbies.entities.Hobby
import com.yelysei.hobbyharbor.model.results.SuccessResult
import com.yelysei.hobbyharbor.model.results.takeSuccess
import com.yelysei.hobbyharbor.ui.dialogs.AddCustomHobbyDialog
import com.yelysei.hobbyharbor.ui.dialogs.SetGoalDialog
import com.yelysei.hobbyharbor.ui.dialogs.prepareDialog
import com.yelysei.hobbyharbor.ui.fab.setMovableBehavior
import com.yelysei.hobbyharbor.ui.screens.main.BaseFragment
import com.yelysei.hobbyharbor.utils.SearchBarOnTextChangeListener
import com.yelysei.hobbyharbor.utils.resources.AttributeUtils
import com.yelysei.hobbyharbor.utils.viewModelCreator

class CategorizedHobbiesFragment : BaseFragment() {
    private val viewModel by viewModelCreator {
        CategorizedHobbiesViewModel(
            hobbiesRepository,
            userHobbiesRepository,
            uiActions,
            stringResources
        )
    }
    private lateinit var binding: FragmentCategorizedHobbiesBinding
    private lateinit var addCustomHobbyDialog: AddCustomHobbyDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentCategorizedHobbiesBinding.inflate(inflater, container, false)

        val availableHobbiesAdapter = setAdapter()
        val searchedHobbiesAdapter = setAdapter()

        searchedHobbiesAdapter.processHobbies(listOf())

        viewModel.categorizedHobbies.observe(viewLifecycleOwner) { result ->
            com.yelysei.hobbyharbor.ui.screens.renderSimpleResult(binding.root, result) {
                availableHobbiesAdapter.processHobbies(it)
            }
        }

        viewModel.searchedHobbies.observe(viewLifecycleOwner) { result ->
            if (result is SuccessResult) {
                searchedHobbiesAdapter.processHobbies(result.data)
            }
        }


        binding.searchView.editText.addTextChangedListener(object :
            SearchBarOnTextChangeListener() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.searchHobbies(s.toString())
                binding.searchView.editText.requestFocus()
            }
        })

        binding.recyclerViewAvailableCategorizedHobbies.adapter = availableHobbiesAdapter
        binding.recyclerViewAvailableCategorizedHobbies.layoutManager =
            LinearLayoutManager(requireContext())

        binding.recyclerViewSearchedCategorizedHobbies.layoutManager =
            LinearLayoutManager(requireContext())
        binding.recyclerViewSearchedCategorizedHobbies.adapter = searchedHobbiesAdapter
        binding.searchView.setupWithSearchBar(binding.searchBar)

        com.yelysei.hobbyharbor.ui.screens.onTryAgain(binding.root) {
            viewModel.tryAgain()
        }

        configureHobbiesRecyclerViews()

        binding.buttonAddCustomHobby.setMovableBehavior()
        val attributeUtils = AttributeUtils(binding.root, R.styleable.FabView)
        val fabColorStateList =
            ColorStateList.valueOf(attributeUtils.getColorFromAttribute(R.styleable.FabView_fabColor))
        attributeUtils.onClear()
        binding.buttonAddCustomHobby.supportImageTintList = fabColorStateList
        binding.buttonAddCustomHobby.setOnClickListener {
            showAddCustomHobbyDialog()
        }

        return binding.root
    }

    private fun showAddCustomHobbyDialog() {
        if (!isAdded || isDetached) {
            return
        }
        addCustomHobbyDialog = AddCustomHobbyDialog(
            requireContext(),
            uiActions,
            stringResources,
            viewModel.categories.value.takeSuccess()
                ?: throw IllegalStateException("Categories have not been loaded")
        ) { hobby ->
            val hobbyExists = viewModel.checkIfHobbyExists(hobby.hobbyName)
            if (hobbyExists) {
                uiActions.toast(
                    stringResources.getString(
                        R.string.hobby_exists_exception,
                        hobby.hobbyName
                    )
                )
            } else {
                addCustomHobbyDialog.dialog.dismiss()
                val setGoalDialog = prepareDialog(onSubmitClickListener = { goal ->
                    viewModel.addCustomHobby(hobby, goal)
                    findNavController().navigate(R.id.userHobbiesFragment)
                })
                setGoalDialog.show()
            }
        }
        addCustomHobbyDialog.show()
    }

    private fun showAddHobbyDialog(hobby: Hobby) {
        if (!isAdded || isDetached) {
            return
        }
        viewModel.checkIfUserHobbyExists(hobby.id ?: throw Exception("Hobby does not have Id"))
            .observe(viewLifecycleOwner) { hobbyExists ->
                if (hobbyExists) {
                    uiActions.toast(
                        stringResources.getString(
                            R.string.hobby_exists_exception,
                            hobby.hobbyName
                        )
                    )
                } else {
                    val setGoalDialog: SetGoalDialog = prepareDialog(onSubmitClickListener = {
                        viewModel.addUserHobby(hobby, it)
                        findNavController().navigate(R.id.userHobbiesFragment)
                    })
                    setGoalDialog.show()
                }
            }
    }

    private fun configureHobbiesRecyclerViews() {
        com.yelysei.hobbyharbor.ui.screens.recyclerViewConfigureView(
            com.yelysei.hobbyharbor.ui.screens.Configuration(
                recyclerView = binding.recyclerViewAvailableCategorizedHobbies,
                layoutManager = LinearLayoutManager(requireContext()),
                verticalItemSpace = 32,
            )
        )
        com.yelysei.hobbyharbor.ui.screens.recyclerViewConfigureView(
            com.yelysei.hobbyharbor.ui.screens.Configuration(
                recyclerView = binding.recyclerViewSearchedCategorizedHobbies,
                layoutManager = LinearLayoutManager(requireContext()),
                verticalItemSpace = 32,
            )
        )
    }

    private fun setAdapter(): HobbiesAdapter {
        return HobbiesAdapter(requireContext()) {
            showAddHobbyDialog(it)
        }
    }
}