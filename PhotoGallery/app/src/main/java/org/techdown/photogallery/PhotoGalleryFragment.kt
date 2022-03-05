package org.techdown.photogallery

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.inflate
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.resources.Compatibility.Api21Impl.inflate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory


private const val TAG="PhotoGalleryFragment"

class PhotoGalleryFragment: Fragment() {

    private lateinit var photoRecyclerView:RecyclerView
    private lateinit var photoGalleryViewModel:PhotoGalleryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        photoGalleryViewModel=ViewModelProvider(this).get(PhotoGalleryViewModel::class.java)



    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("PhotoGalleryFragment","onCreateView")

        val view=inflater.inflate(R.layout.fragment_photo_gallery,container,false)

        photoRecyclerView=view.findViewById(R.id.photo_recycler_view)
        photoRecyclerView.layoutManager=GridLayoutManager(context,3)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        photoGalleryViewModel.galleryItemLiveData.observe(
            viewLifecycleOwner,
            Observer{
                galleryItems->
                Log.d(TAG,"Have gallery from $galleryItems")
                     photoRecyclerView.adapter=PhotoAdapter(galleryItems)


            }
        )
    }

    override fun onDestroy() {
        Log.d("PhotoGalleryFragment","onDestroy")
        super.onDestroy()
    }

    override fun onDestroyView() {
        Log.d("PhotoGalleryFragment","onDestroyView")
        super.onDestroyView()
    }

    override fun onDetach() {
        Log.d("PhotoGalleryFragment","onDetach")
        super.onDetach()
    }



    private class PhotoHolder(private val itemImageView:ImageView):RecyclerView.ViewHolder(itemImageView){
        val bindDrawable:(Drawable)->Unit=itemImageView::setImageDrawable
    }

    private inner class PhotoAdapter(private val galleryItems:List<GalleryItem>)
        :RecyclerView.Adapter<PhotoHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoHolder {
            val view=layoutInflater.inflate(R.layout.list_item_gallery,parent,false) as ImageView
            return PhotoHolder(view)
        }

        override fun getItemCount(): Int {
            return galleryItems.size
        }

        override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
            val galleryItem=galleryItems[position]
            val placeholder:Drawable = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.img1
            )?: ColorDrawable()
            holder.bindDrawable(placeholder)
            // 만일 ContextCompat.getDrawable()함수가 null을 반환할시,
            // place holder에 이미지가 없는 ColorDrawable 객체가 제공된다.
        }

    }





    companion object{
        fun newInstance()=PhotoGalleryFragment()
    }


}