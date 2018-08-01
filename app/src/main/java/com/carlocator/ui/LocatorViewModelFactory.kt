package com.carlocator.ui

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.carlocator.data.LocatorRepository

/**
 * @author ashish <ashish.bhandari>
 */
class LocatorViewModelFactory(private val repository : LocatorRepository) : ViewModelProvider.NewInstanceFactory() {


    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LocatorViewModel(repository) as T
    }
}
