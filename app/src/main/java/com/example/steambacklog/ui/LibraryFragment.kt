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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.*

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class LibraryFragment : Fragment() {

    private val UNPLAYED = 0
    private val IN_PROGRESS = 1
    private val FINISHED = 2

    private val viewModel: LibraryViewModel by activityViewModels()

    //initialize lists and adapters for recyclerview
    private val games = arrayOf(arrayListOf<Games>(), arrayListOf(), arrayListOf())
    private var gamesFilterFull = arrayOf(arrayListOf<Games>(), arrayListOf(), arrayListOf())

    private val gameAdapterUnplayed = GameAdapter(games[UNPLAYED], ::onGameClick)
    private val gameAdapterPlayed = GameAdapter(games[IN_PROGRESS], ::onGameClick)
    private val gameAdapterFinished = GameAdapter(games[FINISHED], ::onGameClick)

    private val gameAdapters = arrayOf(gameAdapterUnplayed, gameAdapterPlayed, gameAdapterFinished)

    private var userID: Long = 0

    //My library: 76561198257218665
    //Friend Library: 76561198078057726

    //add this in the future? https://wiki.teamfortress.com/wiki/WebAPI/ResolveVanityURL

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_library, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_library, menu)
        super.onCreateOptionsMenu(menu, inflater)

        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)

        val searchItem: MenuItem = menu.findItem(R.id.search)
        val searchView: SearchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }
            //called each time the search text gets updated
            override fun onQueryTextChange(query: String?): Boolean {
                if(query != null) {
                    filter(query)
                }
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
        rvLibraryUnplayed.adapter = gameAdapters[UNPLAYED]

        rvLibraryPlayed.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rvLibraryPlayed.adapter = gameAdapters[IN_PROGRESS]

        rvLibraryFinished.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rvLibraryFinished.adapter = gameAdapters[FINISHED]

        createItemTouchHelper(UNPLAYED).attachToRecyclerView(rvLibraryUnplayed)
        createItemTouchHelper(IN_PROGRESS).attachToRecyclerView(rvLibraryPlayed)
        createItemTouchHelper(FINISHED).attachToRecyclerView(rvLibraryFinished)
    }

    /**
     * Check for changes in library dataset
     */
    private fun observeLibrary() {
        viewModel.library.observe(viewLifecycleOwner, {
            CoroutineScope(Dispatchers.IO).launch {
                //clear lists
                for (list in games) list.clear()
                for (list in gamesFilterFull) list.clear()
                //directly add api response to the correct lists
                for (game in it.response.games) {
                    //add new entries on loading api
                    if (viewModel.gameDataOnce(game.appid, userID) == null) {
                        if (game.playtime_forever == 0) {
                            games[UNPLAYED].add(game)
                            viewModel.insertGameData(game.appid, userID, Completion.UNPLAYED)
                        } else {
                            games[IN_PROGRESS].add(game)
                            viewModel.insertGameData(game.appid, userID, Completion.IN_PROGRESS)
                        }
                    } else {
                        when (viewModel.gameDataOnce(game.appid, userID)?.completion) {
                            Completion.UNPLAYED -> games[UNPLAYED].add(game)
                            Completion.IN_PROGRESS -> games[IN_PROGRESS].add(game)
                            Completion.FINISHED -> games[FINISHED].add(game)
                        }
                    }
                }

                //update the full list as well
                CoroutineScope(Dispatchers.Main).launch {
                    for ((index, list) in games.withIndex()) gamesFilterFull[index].addAll(list)

                    for (adapter in gameAdapters) adapter.notifyDataSetChanged()
                }
            }
            Log.i("load", "Finished loading all database entries")
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
        for(list in games) list.clear()

        for((index, list) in gamesFilterFull.withIndex()) {
            for (game in list) {
                if (game.name.toLowerCase(Locale.ROOT).contains(filterString.toLowerCase(Locale.ROOT))) {
                    games[index].add(game)
                }
            }
        }
        for(adapter in gameAdapters) adapter.notifyDataSetChanged()
    }

    /**
     * show the dialog that allows you to change between different user IDs
     */
    private fun showChangeUserDialog(){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.change_user))
        val dialogLayout = layoutInflater.inflate(R.layout.change_user_dialog, null)
        val tvUserID = dialogLayout.findViewById<EditText>(R.id.txt_userID)

        builder.setView(dialogLayout)
        builder.setPositiveButton(R.string.dialog_ok_btn) { _: DialogInterface, _: Int ->
            if(tvUserID.text.isNotEmpty()){
                userID = tvUserID.text.toString().toLong()
                viewModel.getSteamGames(userID)
                observeLibrary()
            }
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
        args.putLong(USER_ID, userID)

        //reset the list and fill it back up in case the user is searching something since the query gets removed without resetting the list
        for (list in games) list.clear()
        for ((index, list) in gamesFilterFull.withIndex()) games[index].addAll(list)

        //move to the gameview fragment with bundle
        findNavController().navigate(R.id.action_LibraryFragment_to_GameviewFragment, args)
    }

    /**
     * Switch between recyclerviews on swipe
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

                //ugly when statement to change completion status of the games
                val selectedGame = games[adapterIndex][position]

                CoroutineScope(Dispatchers.IO).launch {
                    val gameData = viewModel.gameDataOnce(selectedGame.appid, userID)
                    CoroutineScope(Dispatchers.Main).launch {
                        when (adapterIndex) {
                            UNPLAYED -> if (direction == ItemTouchHelper.RIGHT) {
                                viewModel.updateGameData(selectedGame.appid, userID, Completion.IN_PROGRESS, gameData?.rating, gameData?.note)
                                games[adapterIndex + 1].add(selectedGame)
                                gamesFilterFull[adapterIndex + 1].add(selectedGame)
                            }
                            IN_PROGRESS -> {
                                if (direction == ItemTouchHelper.RIGHT) {
                                    viewModel.updateGameData(selectedGame.appid, userID, Completion.FINISHED, gameData?.rating, gameData?.note)
                                    games[adapterIndex + 1].add(selectedGame)
                                    gamesFilterFull[adapterIndex + 1].add(selectedGame)
                                } else {
                                    viewModel.updateGameData(selectedGame.appid, userID, Completion.UNPLAYED, gameData?.rating, gameData?.note)
                                    games[adapterIndex - 1].add(selectedGame)
                                    gamesFilterFull[adapterIndex - 1].add(selectedGame)
                                }
                            }
                            FINISHED -> if (direction == ItemTouchHelper.LEFT) {
                                viewModel.updateGameData(selectedGame.appid, userID, Completion.IN_PROGRESS, gameData?.rating, gameData?.note)
                                games[adapterIndex - 1].add(selectedGame)
                                gamesFilterFull[adapterIndex - 1].add(selectedGame)
                            }
                        }
                        games[adapterIndex].remove(selectedGame)
                        gamesFilterFull[adapterIndex].remove(selectedGame)

                        for (adapter in gameAdapters) adapter.notifyDataSetChanged()
                    }
                }
            }
        }
        return ItemTouchHelper(itemTouchHelperCallback)
    }
}