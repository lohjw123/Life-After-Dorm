package com.example.lifeafterdorm

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.lifeafterdorm.databinding.FragmentRecoverPasswordBinding

class RecoverPasswordFragment : Fragment() {

    private lateinit var binding:FragmentRecoverPasswordBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecoverPasswordBinding.inflate(inflater, container, false)
        val userId = arguments?.getString("userId")
        val view = binding.root

        return view
    }
}