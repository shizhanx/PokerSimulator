package com.example.pokersimulator.listener

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.AnimationDrawable
import android.util.Log
import android.view.DragEvent
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pokersimulator.GameBoardViewModel
import com.example.pokersimulator.R
import com.example.pokersimulator.domain_object.GameActionEnum
import kotlin.math.log
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.text.Html
import androidx.core.graphics.drawable.toBitmap


/**
 * The drag event listener specifically for in game actions to the cards
 */
class MyDragListener(
    private val viewModel: GameBoardViewModel,
    private val logZone: TextView
): View.OnDragListener {
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

        return when (dragEvent.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                if (gameActionType != null){
                    when (view.id) {
                        R.id.yourHand -> view.setBackgroundResource(R.drawable.cards_in_hands_hover)
                        R.id.yourPlayedPile -> view.setBackgroundResource(R.drawable.table_hover_border)
                    }
                }
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
                val player = viewModel.currentPlayerLiveData.value
                when (gameActionType) {
                    // TODO add network related stuff
                    GameActionEnum.DRAW -> {
                        logZone.append(player + viewModel.drawCard())
                    }
                    GameActionEnum.UNDO_DRAW -> {
                        logZone.append(player + viewModel.undoDraw((dragEvent.clipData.getItemAt(0).text as String).toInt()))
                    }
                    GameActionEnum.PLAY -> {
                        logZone.append(player + viewModel.play((dragEvent.clipData.getItemAt(0).text as String).toInt()))
                    }
                    GameActionEnum.UNDO_PLAY -> {
                        logZone.append(player + viewModel.undoPlay((dragEvent.clipData.getItemAt(0).text as String).toInt()))
                    }
                }
                gameActionType != null
            }
            DragEvent.ACTION_DRAG_ENDED -> {
                when (view.id) {
                    R.id.yourHand -> view.setBackgroundResource(R.drawable.cards_in_hands)
                    R.id.yourPlayedPile -> view.setBackgroundResource(R.drawable.table_gold_border)
                }
                gameActionType != null
            }
            else -> false
        }
    }
}
