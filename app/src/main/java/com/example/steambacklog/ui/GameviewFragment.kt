package com.example.steambacklog.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.steambacklog.R
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
        return inflater.inflate(R.layout.fragment_gameview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fillLayout()
    }

    private fun fillLayout(){
        //retrieve object from library fragment
        val gameObj: Games? = arguments?.getParcelable(SELECTED_GAME)

        //set the name of the game
        tvGameName.text = gameObj?.name

        //fill in the playtime
        val timeText: String

        timeText = if(gameObj?.playtime_forever!! < 60) gameObj.playtime_forever.toString()+" Minutes"
        else (gameObj.playtime_forever / 60).toString()+" Hours"

        tvPlaytime.text = timeText

        //load in the image at the top
        context?.let { Glide.with(it).load(gameObj.getLogoUrl()).into(ivGamelogo) }
    }
}