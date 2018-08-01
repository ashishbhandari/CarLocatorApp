package com.carlocator.ui.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout.VERTICAL
import com.carlocator.R
import com.carlocator.adapter.LocatorAdapter
import com.carlocator.ui.LocatorViewModel
import com.carlocator.utils.InjectorUtils
import kotlinx.android.synthetic.main.frag_main.*


/**
 * @author ashish <ashish.bhandari>
 */
class MainFragment : Fragment() {

    private lateinit var viewModel: LocatorViewModel

    companion object {
        fun newInstance(): MainFragment {
            return MainFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.frag_main, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = InjectorUtils.provideLocatorViewModelFactory(view.context)
        viewModel = ViewModelProviders.of(this, factory).get(LocatorViewModel::class.java)

        val locatorAdapter = LocatorAdapter()
        locator_list_item.adapter = locatorAdapter
        locator_list_item.layoutManager = LinearLayoutManager(view.context)
        val decoration = DividerItemDecoration(context, VERTICAL)
        locator_list_item.addItemDecoration(decoration)

        subscribeUI(locatorAdapter)

    }

    private fun subscribeUI(locatorAdapter: LocatorAdapter) {
        viewModel.getPlaceMarkers().observe(viewLifecycleOwner, Observer { placeMarkers ->
            if (placeMarkers?.data != null && placeMarkers.data!!.isNotEmpty()) {

                progress_bar.visibility = View.GONE
                locator_list_item.visibility = View.VISIBLE

                locatorAdapter.updateItems(placeMarkers.data!!)
            } else {
                progress_bar.visibility = View.VISIBLE
                locator_list_item.visibility = View.GONE
            }
        })

    }

}