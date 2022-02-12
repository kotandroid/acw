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
        Log.d(TAG,"onCreateView_crimelistfragment")
        val view = inflater.inflate(R.layout.fragment_crime_list, container, false)

        crimeRecyclerView =
            view.findViewById(R.id.crime_recycler_view) as RecyclerView
        crimeRecyclerView.layoutManager = LinearLayoutWrapper(requireContext(),LinearLayoutManager.VERTICAL,false)

        //recyclerview가 생성된 후에는 곧바로 layoutManager를 설정해주어야 한다.
        //recyclerview는 화면에 배치시키는 일을 본인이 직접하는 것이 아니라, layoutmanager에게 위임한다.
        //위임받은 manager는 화면의 모든 항목의 위치를 처리하고, 스크롤도 처리한다.

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG,"onViewCreated_crimelistfragment")

        super.onViewCreated(view, savedInstanceState)
        //crimeListViewModel이 사용되므로 여기서 by lazy선언에 의해 초기화된다.
        crimeListViewModel.crimeListLiveData.observe(
            viewLifecycleOwner, // fragment의 생명주기를 나타내는 lifecycleowner 구현객체 반환
            Observer { crimes-> //crimes는 crimeListViewModel.crimeListLiveData를 의미한다.
                crimes?.let{
                    Log.i(TAG,"Got crimes ${crimes.size}")
                    updateUI(crimes.toMutableList())
                }
            })


    }

    private fun updateUI(crimes: List<Crime>) {
        Log.d(TAG,"updateUI_crimelistfragment")
        if(adapter==null){
            Log.d(TAG,"updateUI_crimelistfragment_null")
            adapter=CrimeAdapter(diffUtil)
            adapter?.submitList(crimes)
            crimeRecyclerView.adapter=adapter

        }
        else{
            Log.d(TAG,"updateUI_crimelistfragment_submit")
            adapter?.submitList(crimes)
            crimeRecyclerView.adapter=adapter
        }
    }

    private inner class CrimeHolder(view: View)
        : RecyclerView.ViewHolder(view), View.OnClickListener {

        private lateinit var crime: Crime
        /*
        adapter로부터 view를 받아서 변수를 선언해 참조한다.
        여기서 itemView는 RecyclerView.ViewHolder 슈퍼 클래스로 부터 상속 받은 것이고 전달된 View의 참조를 갖는다.
        즉 view와 같기 때문에 밑의 참조코드는 itemView대신 view를 사용해도 무방하다.
         */
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

    private inner class CrimeAdapter(diffCallback: DiffUtil.ItemCallback<Crime>)
      //  :RecyclerView.Adapter<CrimeHolder>(){
        : androidx.recyclerview.widget.ListAdapter<Crime,CrimeHolder>(diffCallback) {
        /* adapter의 역할 : 필요한 viewHolder 인스턴스들을 생성/ data들을 viewholder와 binding한다.
           recycler view 는 viewholder의 생성과 data binding을 adpater에 요청하기만 하면 된다.
         */

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
                : CrimeHolder {
            Log.d(TAG,"onCreateViewHolder")

            val view:View
            when(viewType){
                0 ->  view = layoutInflater.inflate(R.layout.list_item_crime, parent, false)
                else ->  view = layoutInflater.inflate(R.layout.list_item_seriouscrime, parent, false)
            }// recylcer view의 list item을 inflate

            return CrimeHolder(view)
        }


    /*
        override fun getItemViewType(position: Int): Int {
            return crimes[position].requiresPolice
        }
    */
        override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
            // recycler view가 adapter에게 bind요청을 보내면 실행되는 함수라고 생각하면 된다.
            Log.d(TAG,"onBindView")

           // val crime = crimes[position]
           // holder.bind(crime)
            holder.bind(getItem(position))
        }



    }

    override fun onDestroyView() {

        super.onDestroyView()
        Log.d(TAG,"onDestroyView")
    }
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG,"onDestroy" )

    }

}