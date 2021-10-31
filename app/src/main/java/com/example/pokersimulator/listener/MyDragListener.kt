package com.example.pokersimulator.listener

import android.util.Log
import android.view.DragEvent
import android.view.View
import com.example.pokersimulator.GameBoardViewModel
import com.example.pokersimulator.R
import com.example.pokersimulator.domain_object.GameActionEnum

/**
 * The drag event listener specifically for in game actions to the cards
 */
class MyDragListener(private val viewModel: GameBoardViewModel): View.OnDragListener {
    override fun onDrag(view: View, dragEvent: DragEvent): Boolean {
        var gameActionType: GameActionEnum? = null
        // Determine the possible game play option of this drag and drop action
        if (dragEvent.clipDescription != null) {
            if (view.id == R.id.yourHand && dragEvent.clipDescription.label == R.id.drawPile.toString()) {
                gameActionType = GameActionEnum.DRAW
            } else if (view.id == R.id.drawPile && dragEvent.clipDescription.label == R.id.yourHand.toString()) {
                gameActionType = GameActionEnum.UNDO_DRAW
            } else if (view.id == R.id.yourPlayedPile && dragEvent.clipDescription.label == R.id.yourHand.toString()) {
                gameActionType = GameActionEnum.PLAY
            } else if (view.id == R.id.yourHand && dragEvent.clipDescription.label == R.id.yourPlayedPile.toString()) {
                gameActionType = GameActionEnum.UNDO_PLAY
            }
        } else Log.d("TAG", ": null clipDescription, drag event has ended")

        // TODO Add visual effect to highlight entering and exiting a droppable zone
        return when (dragEvent.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                gameActionType != null
            }
            DragEvent.ACTION_DRAG_ENTERED -> {
                gameActionType != null
            }
            DragEvent.ACTION_DRAG_LOCATION -> {
                gameActionType != null
            }
            DragEvent.ACTION_DRAG_EXITED -> {
                gameActionType != null
            }
            DragEvent.ACTION_DROP -> {
                when (gameActionType) {
                    // TODO add network related stuff
                    GameActionEnum.DRAW -> {
                        viewModel.drawCard()
                    }
                    GameActionEnum.UNDO_DRAW -> {
                        viewModel.undoDraw((dragEvent.clipData.getItemAt(0).text as String).toInt())
                    }
                    GameActionEnum.PLAY -> {
                        viewModel.play((dragEvent.clipData.getItemAt(0).text as String).toInt())
                    }
                    GameActionEnum.UNDO_PLAY -> {
                        viewModel.undoPlay((dragEvent.clipData.getItemAt(0).text as String).toInt())
                    }
                }
                gameActionType != null
            }
            DragEvent.ACTION_DRAG_ENDED -> {
                gameActionType != null
            }
            else -> false
        }
    }
}