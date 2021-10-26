package com.example.pokersimulator.common

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class MyOverlapDecorator: RecyclerView.ItemDecoration() {
    // TODO change according to the image of cards we use

   private val HORIZONTAL_OFFSET = -163
  //  private val HORIZONTAL_OFFSET = -1

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        if (parent.getChildAdapterPosition(view) != 0)
            outRect.set(0, 0, HORIZONTAL_OFFSET, 0)
    }
}