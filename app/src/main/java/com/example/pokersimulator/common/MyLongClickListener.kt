package com.example.pokersimulator.common

import android.content.ClipData
import android.content.ClipDescription
import android.view.View

class MyLongClickListener(
    private val label: Int,
    private val clipItem: String,
): View.OnLongClickListener {
    override fun onLongClick(view: View?): Boolean {
        // Each ClipData should consist of the following stuff:
        // label: id of the ViewGroup of this card, like draw pile/you hand/etc
        // content type: array of ClipDescription.MIMETYPE_TEXT_PLAIN
        // ClipData.item: the string representation of this card
        val clipData = ClipData(
            label.toString(),
            arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
            ClipData.Item(clipItem)
        )
        return view!!.startDragAndDrop(clipData, View.DragShadowBuilder(view), null, 0)
    }
}