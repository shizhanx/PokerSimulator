package com.example.pokersimulator.common

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class MyOverlapDecorator(private val offset: Int): RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.set(0, 0, -offset, 0)
    }
}