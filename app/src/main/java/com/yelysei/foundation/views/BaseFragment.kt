package com.yelysei.foundation.views

import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment(){
    abstract val viewModel: BaseViewModel
}