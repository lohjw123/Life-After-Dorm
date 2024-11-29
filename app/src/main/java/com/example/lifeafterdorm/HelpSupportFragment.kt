package com.example.lifeafterdorm

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class HelpSupportFragment : Fragment() {

    private lateinit var myToolbar: Toolbar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val myToolbar = (activity as AppCompatActivity).findViewById<Toolbar>(R.id.navDrawerToolbar)
        myToolbar.title = "Help & Support"

        val view = inflater.inflate(R.layout.fragment_help_support, container, false)
        val arrayAdapter: ArrayAdapter<*>
        val items = resources.getStringArray(R.array.help_support_strings)

        var mListView : ListView = view.findViewById(R.id.lvHelpSupport)
        arrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, items)
        mListView.adapter = arrayAdapter

        mListView.setOnItemClickListener { _, _, position, _ ->
            if (position == 0) {
                replaceFragment(AboutUsFragment())
            } else if (position == 1) {
                replaceFragment(FaqFragment())
            }
        }
        return view
    }

    private fun replaceFragment(fragment: Fragment){
        if(fragment != null){
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainer, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }
}