package com.example.pokersimulator.common

import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.pokersimulator.databinding.UserFragmentBinding
import com.example.pokersimulator.domain_object.PlayerData

/**
 * The adapter for recycler views that want to show a list of users, this includes the list of hosts.
 * Each list item will have the name of the user (possibly string name,
 * but could be device ID or whatever you want to show)
 * and a button next to it. The click event of the buttons will be passed in as a parameter
 */
class MyUsernameRecyclerViewAdapter(
    private val userList:MutableList<String>,
//    userList:MutableList<PlayerData>,
//    data:MutableList<PlayerData>,
//    onClickListener: View.OnClickListener
): RecyclerView.Adapter<MyUsernameRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyUsernameRecyclerViewAdapter.ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = UserFragmentBinding.inflate(LayoutInflater.from(parent.context),
                    parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyUsernameRecyclerViewAdapter.ViewHolder, position: Int) {
//        val currentItem = userList[position]
        holder.usernameView.text = userList[position]
        val str1: String = holder.usernameView.getText().toString()
        Log.d("++++++", str1)
        holder.buttonAction.text = "Join"
    }

    override fun getItemCount(): Int = userList.size

    fun addUser(username: String){
        userList.add(username)
        notifyDataSetChanged()
    }

    inner class ViewHolder(binding: UserFragmentBinding) : RecyclerView.ViewHolder(binding.root) {
        val usernameView = binding.textViewUsername
        val buttonAction = binding.buttonUserAction
    }
}