package com.acw.android.criminalintent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import java.io.File
import java.util.*

private const val PHOTO_PATH="photo_path"

class DialogFragment : Fragment() {
    private lateinit var photoPath:File
    private lateinit var imageView:ImageView

    companion object{
        fun newInstance(photoFile:File):DialogFragment{
            val args=Bundle().apply{
                putSerializable(PHOTO_PATH,photoFile)
            }//Bundle 인자를 구성
            return DialogFragment().apply{
                arguments=args
            }// 구성한 Bundle 인자를 넣은 Fragment를 return
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         photoPath =arguments?.getSerializable(PHOTO_PATH) as File

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dialog, container, false)
        imageView=view.findViewById(R.id.img)


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (photoPath.exists()) {
            val bitmap = getScaledBitmap(photoPath.path, requireActivity())
            imageView.setImageBitmap(bitmap)
        }

    }



    override fun onStart() {
        super.onStart()


    }



    }