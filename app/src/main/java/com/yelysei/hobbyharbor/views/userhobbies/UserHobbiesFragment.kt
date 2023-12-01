package com.yelysei.hobbyharbor.views.userhobbies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.LinearLayoutManager
import com.yelysei.hobbyharbor.R
import com.yelysei.hobbyharbor.databinding.FragmentUserHobbiesBinding
import com.yelysei.hobbyharbor.model.hobbies.UserHobby
import com.yelysei.hobbyharbor.views.VerticalSpaceItemDecoration
import com.yelysei.hobbyharbor.views.base.BaseFragment
import com.yelysei.hobbyharbor.views.base.BaseScreen
import com.yelysei.hobbyharbor.views.base.screenViewModel


class UserHobbiesFragment : BaseFragment(){

    class Screen : BaseScreen


    override val viewModel by screenViewModel<UserHobbiesViewModel>()

    private lateinit var binding: FragmentUserHobbiesBinding
    private lateinit var adapter: UserHobbiesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserHobbiesBinding.inflate(inflater, container, false)
        adapter = UserHobbiesAdapter(object: UserHobbyActionListener {
            override fun onUserHobbyDetails(userHobby: UserHobby) {
                viewModel.onUserHobbyPressed(userHobby.id)
            }
        })

        viewModel.userHobbies.observe(viewLifecycleOwner) {
            adapter.userHobbies = it
        }

        //recycler view look configuration
        val layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewUserHobbies.layoutManager = layoutManager
        //recycler view space between items
        binding.recyclerViewUserHobbies.addItemDecoration(
            VerticalSpaceItemDecoration(
            VERTICAL_ITEM_SPACE
        )
        )
        binding.recyclerViewUserHobbies.adapter = adapter

        //recycler view max height 75% of screen height
        val constraintSet = ConstraintSet()
        constraintSet.clone(binding.constraintLayout)
        val maxHeightInPixels = (0.65 * resources.displayMetrics.heightPixels).toInt()
        constraintSet.constrainHeight(R.id.recyclerViewUserHobbies, ConstraintSet.WRAP_CONTENT)
        constraintSet.constrainMaxHeight(R.id.recyclerViewUserHobbies, maxHeightInPixels)

        constraintSet.applyTo(binding.constraintLayout)

        return binding.root
    }

    companion object {
        const val VERTICAL_ITEM_SPACE = 64
    }

}