package com.example.steambacklog.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.steambacklog.R
import com.example.steambacklog.model.Completion
import com.example.steambacklog.model.Games
import kotlinx.android.synthetic.main.fragment_gameview.*

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */

const val SELECTED_GAME = "arg_selected_game"

class GameviewFragment : Fragment() {

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

        super.onCreateOptionsMenu(menu, inflater)
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

        //fill in completion status
        when(gameObj.completion){
            Completion.UNPLAYED -> tvCompletion.append(getString(R.string.tv_unplayed))
            Completion.IN_PROGRESS -> tvCompletion.append(getString(R.string.tv_in_progress))
            Completion.FINISHED -> tvCompletion.append(getString(R.string.tv_finished))
        }

        //load in the image at the top
        context?.let { Glide.with(it).load(gameObj.getLogoUrl()).into(ivGamelogo) }
    }
}