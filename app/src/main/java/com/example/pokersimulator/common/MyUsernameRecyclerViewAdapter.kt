package com.example.pokersimulator.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pokersimulator.databinding.UserFragmentBinding


/**
 * The adapter for recycler views that want to show a list of users, this includes the list of hosts.
 * Each list item will have the name of the user (possibly string name,
 * but could be device ID or whatever you want to show)
 * and a button next to it. The click event of the buttons will be passed in as a parameter
 */
class MyUsernameRecyclerViewAdapter(
    private val userList:MutableList<String>,
    private val username:String,
    private val isHost: Boolean,
    private val inRoom: Boolean,
    private val clickListener: (String) -> Unit,
    private val buttonName: String

) : RecyclerView.Adapter<MyUsernameRecyclerViewAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyUsernameRecyclerViewAdapter.ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = UserFragmentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyUsernameRecyclerViewAdapter.ViewHolder, position: Int) {
        val currentUser = userList[position]
        holder.usernameView.text = userList[position]

        //block of
        if(currentUser == username){
            holder.itemView.visibility = View.GONE
            holder.itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
        }
        else{
            holder.itemView.visibility = View.VISIBLE
            holder.itemView.layoutParams =
                RecyclerView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
        }

        when {
            isHost -> holder.buttonAction.text = "Accept"
            inRoom -> holder.buttonAction.visibility = View.GONE
            else -> holder.buttonAction.text = "Join"
        }

//        holder.usernameView.text = userList[position]
        holder.bind(getItem(position), clickListener)
//        if(isHost)
//            holder.buttonAction.text = "Accept"
//        else
//            holder.buttonAction.text = "Join"


    }

    override fun getItemCount(): Int = userList.size

    private fun getItem(position: Int) = userList[position]

    fun addUser(username: String) {
        userList.add(username)
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: UserFragmentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val usernameView = binding.textViewUsername
        val buttonAction = binding.buttonUserAction

        fun bind(
            item: String,
            clickListener: (String) -> Unit
        ) {
            buttonAction.text = buttonName
            buttonAction.setOnClickListener { clickListener(item) }
        }
    }
}