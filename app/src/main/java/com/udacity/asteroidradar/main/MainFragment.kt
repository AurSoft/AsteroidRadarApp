package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.api.NasaApiStatus
import com.udacity.asteroidradar.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private val viewModel: MainViewModel by lazy {
        val activity = requireNotNull(this.activity) { "ViewModel can be accessed only after onViewCreated()" }
        ViewModelProvider(this, MainViewModelFactory(activity.application)).get(MainViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        binding.asteroidRecycler.adapter = AsteroidAdapter(AsteroidAdapter.OnClickListener{
            viewModel.displayAsteroidDetails(it)
        })

        binding.swipeContainer.setOnRefreshListener {
            viewModel.getLatestAsteroids()
            if(viewModel.picOfDayStatus.value == NasaApiStatus.ERROR) {
                viewModel.getPicOfTheDay()
            }
        }

        viewModel.asteroidListStatus.observe(viewLifecycleOwner, Observer { status ->
            if (status == NasaApiStatus.ERROR) {
                Snackbar.make(
                        requireActivity().findViewById(android.R.id.content),
                        getString(R.string.status_snackbar_text),
                        Snackbar.LENGTH_INDEFINITE
                ).setAction(R.string.ok) {
                    viewModel.setAsteroidListStatusToDone()
                }.show()
            }
        })

        viewModel.navigateToSelectedAsteroid.observe(viewLifecycleOwner, Observer {
            if (null != it) {
                this.findNavController().navigate(MainFragmentDirections.actionShowDetail(it))
                viewModel.displayPropertyDetailsComplete()
            }
        })

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.show_week_menu -> viewModel.filterWeeklyAsteroids()
            R.id.show_today_menu -> viewModel.filterTodayAsteroids()
        }
        return true
    }
}
