package com.example.pokersimulator.listener

import android.content.ClipData
import android.content.ClipDescription
import android.view.View

class MyLongClickListener(
    private val label: Int,
    private val clipItem: String,
): View.OnLongClickListener {
    companion object {
        @JvmStatic var isTurn = false
    }

    override fun onLongClick(view: View?): Boolean {
        return if (isTurn) {
            // Each ClipData should consist of the following stuff:
            // label: id of the ViewGroup of this card, like draw pile/you hand/etc
            // content type: array of ClipDescription.MIMETYPE_TEXT_PLAIN
            // ClipData.item: the position of this card in the pile
            val clipData = ClipData(
                label.toString(),
                arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
                ClipData.Item(clipItem)
            )
            view!!.startDragAndDrop(clipData, View.DragShadowBuilder(view), null, 0)
        } else false
    }
}