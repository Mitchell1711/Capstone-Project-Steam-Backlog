package com.example.steambacklog.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.steambacklog.R
import com.example.steambacklog.model.Games
import com.example.steambacklog.recyclerview.GameAdapter
import com.example.steambacklog.viewmodel.LibraryViewModel
import kotlinx.android.synthetic.main.fragment_library.*

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class LibraryFragment : Fragment() {

    private val viewModel: LibraryViewModel by activityViewModels()
    private val games = arrayListOf<Games>()
    private val gameAdapter = GameAdapter(games, ::onGameClick)

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_library, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()

        viewModel.getSteamGames(76561198257218665)

        observeLibrary()
    }

    /**
     * check this for drag drop between 2 recyclerviews
     * https://github.com/jkozh/DragDropTwoRecyclerViews
     */

    private fun initViews(){
        //init layout
        rvLibraryUnplayed.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rvLibraryUnplayed.adapter = gameAdapter
    }

    private fun observeLibrary() {
        viewModel.library.observe(viewLifecycleOwner, {
            games.clear()
            games.addAll(it.response.games)

            gameAdapter.notifyDataSetChanged()
        })

        // Observe the error message.
        viewModel.errorText.observe(viewLifecycleOwner, {
            Toast.makeText(activity, it, Toast.LENGTH_SHORT).show()
        })
    }

    private fun onGameClick(game: Games){
        //wrap selected game object in a bundle
        val args = Bundle()
        args.putParcelable(SELECTED_GAME, game)

        //move to the gameview fragment with bundle
        findNavController().navigate(R.id.action_LibraryFragment_to_GameviewFragment, args)
    }
}