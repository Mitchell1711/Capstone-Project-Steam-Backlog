package com.example.steambacklog.ui

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.steambacklog.R
import com.example.steambacklog.model.Completion
import com.example.steambacklog.model.Games
import com.example.steambacklog.viewmodel.LibraryViewModel
import kotlinx.android.synthetic.main.fragment_gameview.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */

const val SELECTED_GAME = "arg_selected_game"
const val USER_ID = "arg_user_id"

class GameviewFragment : Fragment() {

    private val viewModel: LibraryViewModel by activityViewModels()

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_gameview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fillLayout()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater){
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        inflater.inflate(R.menu.menu_gameview, menu)
        super.onCreateOptionsMenu(menu, inflater)

        val saveItem: MenuItem = menu.findItem(R.id.save)

        saveItem.setOnMenuItemClickListener{
            saveData()
            true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                findNavController().popBackStack()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun fillLayout(){
        //retrieve object from library fragment
        val gameObj: Games? = arguments?.getParcelable(SELECTED_GAME)
        val userID = requireArguments().getLong(USER_ID)

        //set the name of the game
        tvGameName.text = gameObj?.name

        //fill in the playtime
        var timeText: String

        if(gameObj?.playtime_forever!! < 60){
            timeText = gameObj.playtime_forever.toString()+" Minute"
            if(gameObj.playtime_forever != 1) timeText += "s"
        }
        else{
            timeText = (gameObj.playtime_forever / 60).toString()+" Hour"
            if(gameObj.playtime_forever >= 120) timeText += "s"
        }

        tvPlaytime.append(timeText)

        //load in the image at the top
        context?.let { Glide.with(it).load(gameObj.getLogoUrl()).into(ivGamelogo) }

        CoroutineScope(Dispatchers.IO).launch {
            val gameData = viewModel.gameDataOnce(gameObj.appid, userID)

            CoroutineScope(Dispatchers.Main).launch {
                //fill in completion status
                when (gameData?.completion) {
                    Completion.UNPLAYED -> tvCompletion.append(getString(R.string.tv_unplayed))
                    Completion.IN_PROGRESS -> tvCompletion.append(getString(R.string.tv_in_progress))
                    Completion.FINISHED -> tvCompletion.append(getString(R.string.tv_finished))
                }

                tvNote.setText(gameData?.note)
                ratingBar.rating = gameData?.rating!!
            }
        }
    }

    private fun saveData(){
        //retrieve object from library fragment here too
        val gameObj: Games? = arguments?.getParcelable(SELECTED_GAME)
        val userID = requireArguments().getLong(USER_ID)

        Log.e("rating", ratingBar.rating.toString())

        viewModel.gameData(gameObj!!.appid, userID)?.observe(viewLifecycleOwner, {
            viewModel.updateGameData(gameObj.appid, userID, it.completion, ratingBar.rating, tvNote.text.toString())
        })

        Toast.makeText(activity, R.string.updatetoast, Toast.LENGTH_SHORT).show()
    }
}