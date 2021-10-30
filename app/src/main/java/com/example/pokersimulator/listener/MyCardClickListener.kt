package com.example.pokersimulator.listener

import android.view.View
import com.example.pokersimulator.GameBoardViewModel

class MyCardClickListener(
    private val viewModel: GameBoardViewModel,
    private val pileId: Int,
    private val position: Int,
): View.OnClickListener {
    companion object {
        @JvmStatic var isTurn = false
    }

    override fun onClick(view: View?) {
        // TODO add network related stuff
        if (isTurn)
            viewModel.flipCard(pileId, position)
    }
}