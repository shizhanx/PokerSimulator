package com.example.pokersimulator.listener

import android.view.View
import com.example.pokersimulator.GameBoardViewModel

class MyCardClickListener(
    private val viewModel: GameBoardViewModel,
    private val pileId: Int,
    private val position: Int,
): View.OnClickListener {

    override fun onClick(view: View?) {
        viewModel.flipCard(pileId, position)
    }
}