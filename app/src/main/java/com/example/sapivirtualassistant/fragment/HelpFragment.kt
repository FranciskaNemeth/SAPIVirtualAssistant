package com.example.sapivirtualassistant.fragment

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sapivirtualassistant.R
import com.example.sapivirtualassistant.adapter.HelpRecyclerViewAdapter
import com.example.sapivirtualassistant.database.DatabaseManager
import com.example.sapivirtualassistant.interfaces.GetHelpModelInterface
import com.example.sapivirtualassistant.model.HelpModel
import com.example.sapivirtualassistant.utils.UtilsClass
import java.util.*
import kotlin.collections.ArrayList

class HelpFragment : Fragment() {
    var helpList : MutableList<HelpModel> = ArrayList()
    var filteredHelpList : MutableList<HelpModel> = ArrayList()
    lateinit var recyclerView : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.help_screen, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        val adapter = HelpRecyclerViewAdapter(filteredHelpList)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        DatabaseManager.getHelpData(object : GetHelpModelInterface {
            override fun getHelpModel(helpList: MutableList<HelpModel>) {
                this@HelpFragment.helpList = helpList
                filteredHelpList.clear()
                filteredHelpList.addAll(helpList)
                recyclerView.adapter!!.notifyDataSetChanged()
            }
        })

        val searchView : androidx.appcompat.widget.SearchView = view.findViewById(R.id.searchView)
        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    filterHelpBySearch(newText)
                }
                return false
            }
        })

        searchView.setIconifiedByDefault(false)

        val topicAI : Button = view.findViewById(R.id.buttonTopicAI)
        topicAI.setOnClickListener {
            filterHelpByTopic("ai")
        }

        val topicFeedback : Button = view.findViewById(R.id.buttonTopicFeedback)
        topicFeedback.setOnClickListener {
            filterHelpByTopic("feedback")
        }

        val topicLogin : Button = view.findViewById(R.id.buttonTopicLogin)
        topicLogin.setOnClickListener {
            filterHelpByTopic("login")
        }

        val topicTimeTable : Button = view.findViewById(R.id.buttonTopicTimetable)
        topicTimeTable.setOnClickListener {
            filterHelpByTopic("timetable")
        }

        val buttonAll : Button = view.findViewById(R.id.buttonAll)
        buttonAll.setOnClickListener {
            filteredHelpList.clear()
            filteredHelpList.addAll(helpList)
            recyclerView.adapter!!.notifyDataSetChanged()
        }

        val itemDecoration = DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL)
        val drawable = GradientDrawable(
            GradientDrawable.Orientation.BOTTOM_TOP, intArrayOf(-0x7373730, -0x7373730)
        )
        drawable.setSize(1, 5)
        itemDecoration.setDrawable(drawable)
        recyclerView.addItemDecoration(itemDecoration)

        return view
    }

    override fun onResume() {
        if( !UtilsClass().isNetworkAvailable(requireContext()) ) {
            AlertDialogFragment().errorHandling(requireContext())
        }

        /*if(!UtilsClass().isInternetAvailable()) {
            AlertDialogFragment().errorHandling(requireContext())
        }*/

        super.onResume()
    }

    fun filterHelpBySearch(searchText : String?) {
        val arrayList : ArrayList<HelpModel> = ArrayList()
        if (searchText != null && searchText.isNotEmpty() && helpList.isNotEmpty()) {
            helpList.forEach {
                if (it.question.toLowerCase(Locale.getDefault()).contains(searchText.toLowerCase(Locale.getDefault()))) {
                    arrayList.add(it)
                }
            }
        }
        else {
            arrayList.addAll(helpList)
        }

        filteredHelpList.clear()
        filteredHelpList.addAll(arrayList)
        recyclerView.adapter!!.notifyDataSetChanged()
    }

    fun filterHelpByTopic(type : String?) {
        val arrayList : ArrayList<HelpModel> = ArrayList()
        if (type != null && type.isNotEmpty() && helpList.isNotEmpty()) {
            helpList.forEach {
                if (it.type.toLowerCase(Locale.getDefault()).contains(type.toLowerCase(Locale.getDefault()))) {
                    arrayList.add(it)
                }
            }
        }
        else {
            arrayList.addAll(helpList)
        }

        filteredHelpList.clear()
        filteredHelpList.addAll(arrayList)
        recyclerView.adapter!!.notifyDataSetChanged()
    }
}