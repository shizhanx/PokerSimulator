package com.example.pokersimulator.listener

import android.view.View
import android.widget.TextView
import com.example.pokersimulator.GameBoardViewModel

class MyCardClickListener(
    private val viewModel: GameBoardViewModel,
    private val pileId: Int,
    private val position: Int,
    private val logZone: TextView
): View.OnClickListener {
    companion object {
        @JvmStatic var isTurn = false
    }

    override fun onClick(view: View?) {
        // TODO add network related stuff
        if (isTurn) {
            val message = viewModel.flipCard(pileId, position)
            if (message != "")
                logZone.append(viewModel.currentPlayerLiveData.value + message)
        }
    }
}