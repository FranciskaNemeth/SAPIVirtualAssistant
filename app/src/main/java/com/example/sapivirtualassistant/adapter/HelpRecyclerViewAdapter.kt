package com.example.sapivirtualassistant.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.sapivirtualassistant.R
import com.example.sapivirtualassistant.model.HelpModel

class HelpRecyclerViewAdapter(dataSet: MutableList<HelpModel>) : RecyclerView.Adapter<HelpRecyclerViewAdapter.ViewHolder>(){
    private val dataList: MutableList<HelpModel>

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.listitem_help, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.question.text = dataList[position].question
        holder.answer.text = dataList[position].answer
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) { //, View.OnClickListener  {
        var question: TextView
        var answer : TextView
        var parenLayout: ConstraintLayout

        init {
            question = itemView.findViewById(R.id.textViewQuestion)
            answer = itemView.findViewById(R.id.textViewAnswer)
            parenLayout = itemView.findViewById(R.id.constraintLayoutHelpListitem)
            //itemView.setOnClickListener(this)
        }

    }

    init {
        this.dataList = dataSet
    }
}