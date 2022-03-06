package org.techdown.photogallery

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.view.View.inflate
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.resources.Compatibility.Api21Impl.inflate
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
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
    private lateinit var thumbnailDownloader:ThumbnailDownloader<PhotoHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance=true// 프래그먼트를 유보 - > 장치 구성이 변경 될 시에도 상태 정보를 계속 보존
        setHasOptionsMenu(true)

        photoGalleryViewModel=ViewModelProvider(this).get(PhotoGalleryViewModel::class.java)


        val responseHandler= Handler()
        thumbnailDownloader=
            ThumbnailDownloader(responseHandler){photoHolder,bitmap->
                val drawable = BitmapDrawable(resources,bitmap)
                photoHolder.bindDrawable(drawable)
            }
        lifecycle.addObserver(thumbnailDownloader.fragmentLifecycleObserver)



    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_photo_gallery,menu)

        val searchItem:MenuItem=menu.findItem(R.id.menu_item_search)
        val searchView=searchItem.actionView as SearchView

        searchView.apply{

            setOnQueryTextListener(object: SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String): Boolean {
                    Log.d(TAG,"query submit : $query")
                    photoGalleryViewModel.fetchPhotos(query)
                    return true
                    //true를 반환하면 검색 요청이 처리되었음을 시스템에 알린다.
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    Log.d(TAG,"Query Changed:$newText")
                    return false
                    // onQueryTextChange는 search view에서 글자의 변경이 발생하는 즉시 실행된다.
                    // 이 앱에서는 사용하지 않으므로, false를 return하여 별도의 처리를 하지 않음을 알린다.
                }
            })
            setOnSearchClickListener{
                searchView.setQuery(photoGalleryViewModel.searchTerm,false)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.menu_item_clear->{
                photoGalleryViewModel.fetchPhotos("")
                true
            }
            else ->super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("PhotoGalleryFragment","onCreateView")
        viewLifecycleOwner.lifecycle.addObserver(thumbnailDownloader.viewLifecycleObserver)
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
        lifecycle.removeObserver(thumbnailDownloader.fragmentLifecycleObserver)
    }

    override fun onDestroyView() {
        Log.d("PhotoGalleryFragment","onDestroyView")
        super.onDestroyView()
        viewLifecycleOwner.lifecycle.removeObserver(thumbnailDownloader.viewLifecycleObserver)
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

            thumbnailDownloader.queueThumbnail(holder,galleryItem.url)
        }

    }





    companion object{
        fun newInstance()=PhotoGalleryFragment()
    }


}