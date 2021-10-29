package com.example.pokersimulator.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pokersimulator.databinding.UserFragmentBinding

/**
 * The adapter for recycler views that want to show a list of users. Each list item will have
 * the name of the user (possibly string name, but could be device ID or whatever you want to show)
 * and a button next to it. The click event of the buttons will be passed in as a parameter
 */
class MyUserRecyclerViewAdapter:  RecyclerView.Adapter<MyUserRecyclerViewAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyUserRecyclerViewAdapter.ViewHolder {
        return ViewHolder(UserFragmentBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: MyUserRecyclerViewAdapter.ViewHolder, position: Int) {
        // TODO Not yet implemented
    }

    override fun getItemCount(): Int {
        //TODO Not yet implemented
        return 0
    }

    inner class ViewHolder(binding: UserFragmentBinding) : RecyclerView.ViewHolder(binding.root) {
    }
}