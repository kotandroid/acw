package com.acw.android.criminalintent

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item_crime.*
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "CrimeListFragment"

class CrimeListFragment : Fragment() {

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<Crime>() {

            override fun areContentsTheSame(oldItem: Crime, newItem: Crime): Boolean {
                Log.d(TAG,"sdsd")
                return oldItem == newItem
            }


            override fun areItemsTheSame(oldItem: Crime, newItem: Crime) =
                oldItem.id == newItem.id
        }
        fun newInstance(): CrimeListFragment {
            return CrimeListFragment()
        }
    }

    interface Callbacks{
        fun onCrimeSelected(crimeId: UUID)
    }

    private var callbacks:Callbacks?=null


    private lateinit var crimeRecyclerView: RecyclerView
    private var adapter: CrimeAdapter? = null

    private val crimeListViewModel: CrimeListViewModel by lazy {
        ViewModelProvider(this).get(CrimeListViewModel::class.java)
    }



    override fun onAttach(context: Context) {
        super.onAttach(context)
        super.onAttach(context)
        callbacks=context as Callbacks?
        //activity의 context를 callback에 할당
        //activity는 context의 서브 클래스이다. 따라서 인자로 activity를 전달해도 무방하다.(but activity를 전달하는 onAttach는 향후 버전에서 deprecated될 확률있음)
        //context를 Callbacks type으로 할당하여 반드시 Callbacks interface를 구현하도록 했다.
        Toast.makeText(context,"onCreate_crimelist",Toast.LENGTH_SHORT).show()
    }

    override fun onDetach() {
        super.onDetach()
        callbacks=null
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime_list, container, false)

        crimeRecyclerView =
            view.findViewById(R.id.crime_recycler_view) as RecyclerView
        crimeRecyclerView.layoutManager = LinearLayoutManager(context)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crimeListViewModel.crimeListLiveData.observe(
            viewLifecycleOwner, // fragment의 생명주기를 나타내는 lifecycleowner 구현객체 반환
            Observer { crimes->
                crimes?.let{
                    Log.i(TAG,"Got crimes ${crimes.size}")
                    updateUI(crimes)
                }
            })

    }

    private fun updateUI(crimes: List<Crime>) {

        adapter=CrimeAdapter(crimes)


        crimeRecyclerView.adapter = adapter
    }

    private inner class CrimeHolder(view: View)
        : RecyclerView.ViewHolder(view), View.OnClickListener {

        private lateinit var crime: Crime

        private val titleTextView: TextView = itemView.findViewById(R.id.crime_title)
        private val dateTextView: TextView = itemView.findViewById(R.id.crime_date)
        private val solvedImageView: ImageView =itemView.findViewById(R.id.crime_solved)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(crime: Crime) { //view의 data 변경이 있을 시 변경
            this.crime = crime
            titleTextView.text = this.crime.title

            val time_format: SimpleDateFormat = SimpleDateFormat("EEE, d MMM yyyy")
            dateTextView.text = time_format.format(this.crime.date).toString()

            solvedImageView.visibility=if(crime.isSolved){
                View.VISIBLE
            }else{
                View.GONE
            }
        }

        override fun onClick(v: View) {
            callbacks?.onCrimeSelected(crime.id)
        }
    }

    private inner class CrimeAdapter(var crimes: List<Crime>)
      //  :RecyclerView.Adapter<CrimeHolder>(){
        : androidx.recyclerview.widget.ListAdapter<Crime,CrimeHolder>(diffUtil) {


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
                : CrimeHolder {
            Log.d(TAG,"onCreateViewHolder")

            val view:View
            when(viewType){
                0 ->  view = layoutInflater.inflate(R.layout.list_item_crime, parent, false)
                else ->  view = layoutInflater.inflate(R.layout.list_item_seriouscrime, parent, false)
            }

            return CrimeHolder(view)
        }

        override fun getItemCount() = crimes.size

        /*override fun getItemViewType(position: Int): Int {
            return crimes[position].requiresPolice
        }*/

        override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
            Log.d(TAG,"onBindView")

            val crime = crimes[position]

            holder.bind(crime)
        }


    }


}