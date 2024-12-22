package com.example.testvk.moviescreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.testvk.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FullDescriptionBottomSheet : BottomSheetDialogFragment() {
    private lateinit var shortDescription: TextView
    private lateinit var fullDescription: TextView


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fulldescription_bottom_sheet, container, false)
        shortDescription = view.findViewById(R.id.shortDescription)
        fullDescription = view.findViewById(R.id.fullDescription)

        val shortDescriptionString = arguments?.getString("shortDESCRIPTION")
        val fullDescriptionString = arguments?.getString("fullDESCRIPTION")
        shortDescription.text = shortDescriptionString
        fullDescription.text = fullDescriptionString

        return view
    }

    companion object {
        const val TAG = "FullDescriptionBottomSheet"
    }
}
