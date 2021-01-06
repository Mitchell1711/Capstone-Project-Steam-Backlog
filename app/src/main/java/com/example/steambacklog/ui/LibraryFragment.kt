package com.example.steambacklog.ui

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.SearchView
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.steambacklog.R
import com.example.steambacklog.model.Completion
import com.example.steambacklog.model.Games
import com.example.steambacklog.recyclerview.GameAdapter
import com.example.steambacklog.viewmodel.LibraryViewModel
import kotlinx.android.synthetic.main.fragment_library.*
import java.util.*

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class LibraryFragment : Fragment() {

    private val viewModel: LibraryViewModel by activityViewModels()

    //initialize lists and adapters for recyclerview
    private val games = arrayOf(arrayListOf<Games>(), arrayListOf(), arrayListOf())

    private val gameAdapterUnplayed = GameAdapter(games[0], ::onGameClick)
    private val gameAdapterPlayed = GameAdapter(games[1], ::onGameClick)
    private val gameAdapterFinished = GameAdapter(games[2], ::onGameClick)

    //private val userID = 76561198078057726
    //76561198257218665

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_library, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        val searchItem: MenuItem = menu.findItem(R.id.search)
        val searchView: SearchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }
            //called each time the search text gets updated
            override fun onQueryTextChange(query: String?): Boolean {
                if(query != null) filter(query)
                return true
            }
        })

        val changeUserItem: MenuItem = menu.findItem(R.id.userID)

        changeUserItem.setOnMenuItemClickListener {
            showChangeUserDialog()
            true
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
    }

    /**
     * check this for drag drop between 2 recyclerviews
     * https://github.com/jkozh/DragDropTwoRecyclerViews
     */

    private fun initViews(){
        //init layout
        rvLibraryUnplayed.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rvLibraryUnplayed.adapter = gameAdapterUnplayed

        rvLibraryPlayed.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rvLibraryPlayed.adapter = gameAdapterPlayed

        rvLibraryFinished.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rvLibraryFinished.adapter = gameAdapterFinished

        createItemTouchHelper(0).attachToRecyclerView(rvLibraryUnplayed)
        createItemTouchHelper(1).attachToRecyclerView(rvLibraryPlayed)
        createItemTouchHelper(2).attachToRecyclerView(rvLibraryFinished)
    }

    /**
     * Check for changes in library dataset
     */
    private fun observeLibrary() {
        viewModel.library.observe(viewLifecycleOwner, {
            for (list in games) list.clear()

            for(game in it.response.games){
                if(game.playtime_forever == 0) {
                    games[0].add(game)
                    game.completion = Completion.UNPLAYED
                }
                else {
                    games[1].add(game)
                    game.completion = Completion.IN_PROGRESS
                }
            }

            gameAdapterUnplayed.notifyDataSetChanged()
        })

        // Observe the error message.
        viewModel.errorText.observe(viewLifecycleOwner, {
            Toast.makeText(activity, it, Toast.LENGTH_SHORT).show()
        })
    }

    /**
     * filter library based on toolbar search input
     */
    private fun filter(filterString: String){
        val gamesFilter = arrayOf(arrayListOf<Games>(), arrayListOf(), arrayListOf())

        for((index, list) in games.withIndex()) {
            for (game in list) {
                if (game.name.toLowerCase().contains(filterString.toLowerCase())) {
                    gamesFilter[index].add(game)
                }
            }
        }
        gameAdapterUnplayed.filterList(gamesFilter[0])
        gameAdapterPlayed.filterList(gamesFilter[1])
        gameAdapterFinished.filterList(gamesFilter[2])
    }

    /**
     * show the dialog that allows you to change between different user IDs
     */
    private fun showChangeUserDialog(){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.change_user))
        val dialogLayout = layoutInflater.inflate(R.layout.change_user_dialog, null)
        val userID = dialogLayout.findViewById<EditText>(R.id.txt_userID)

        builder.setView(dialogLayout)
        builder.setPositiveButton(R.string.dialog_ok_btn) { _: DialogInterface, _: Int ->
            viewModel.getSteamGames(userID.text.toString().toLong())
            observeLibrary()
        }
        builder.show()
    }

    /**
     * check if a game object inside the recyclerview got selected
     */
    private fun onGameClick(game: Games){
        //wrap selected game object in a bundle
        val args = Bundle()
        args.putParcelable(SELECTED_GAME, game)

        //move to the gameview fragment with bundle
        findNavController().navigate(R.id.action_LibraryFragment_to_GameviewFragment, args)
    }

    /**
     * YEAH
     */
    private fun createItemTouchHelper(adapterIndex: Int): ItemTouchHelper {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            // Enables or Disables the ability to move items up and down.
            override fun onMove(
                    recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            // Callback triggered when a user swiped an item.
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                //ugly switch statement to change completion status of the games
                when (adapterIndex){
                    0 -> if(direction == ItemTouchHelper.RIGHT) {
                        games[0][position].completion = Completion.IN_PROGRESS
                        games[1].add(games[0][position])
                    }
                    1 -> {
                        if(direction == ItemTouchHelper.RIGHT) {
                            games[1][position].completion = Completion.FINISHED
                            games[2].add(games[1][position])
                        }
                        else {
                            games[1][position].completion = Completion.UNPLAYED
                            games[0].add(games[1][position])
                        }
                    }
                    2 -> if(direction == ItemTouchHelper.LEFT) {
                        games[2][position].completion = Completion.IN_PROGRESS
                        games[1].add(games[2][position])
                    }
                }
                games[adapterIndex].removeAt(position)

                gameAdapterUnplayed.notifyDataSetChanged()
                gameAdapterPlayed.notifyDataSetChanged()
                gameAdapterFinished.notifyDataSetChanged()
            }
        }
        return ItemTouchHelper(itemTouchHelperCallback)
    }
}