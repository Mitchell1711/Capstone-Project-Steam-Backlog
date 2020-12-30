package com.example.steambacklog.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.steambacklog.R
import com.example.steambacklog.viewmodel.LibraryViewModel
import kotlinx.android.synthetic.main.fragment_library.*

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class LibraryFragment : Fragment() {

    private val viewModel: LibraryViewModel by activityViewModels()

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_library, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getSteamGames(76561198257218665)

        observeLibrary()
    }

    private fun observeLibrary() {
        viewModel.library.observe(viewLifecycleOwner, Observer {
            tv_libraryCount.text = it?.response?.games?.get(1)?.name
        })

        // Observe the error message.
        viewModel.errorText.observe(viewLifecycleOwner, Observer {
            Toast.makeText(activity, it, Toast.LENGTH_SHORT).show()
        })
    }
}