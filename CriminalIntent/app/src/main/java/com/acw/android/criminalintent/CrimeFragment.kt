package com.acw.android.criminalintent

import android.app.ActivityManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.Observer
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.hours
import kotlin.time.toDuration

private const val DIALOG_DATE="DialogDate" //dialog를 식별하기 위한 인자
private const val DIALOG_TIME="DialogTime"
private const val REQUEST_DATE=0
private const val REQUEST_TIME=1
private const val ARG_CRIME_ID="crime_id"
class CrimeFragment() : Fragment(),DatePickerFragment.Callbacks,TimePickerFragment.Callbacks {

    private lateinit var crime: Crime
    private lateinit var titleField: EditText
    private lateinit var dateButton: Button
    private lateinit var timeButton: Button
    private lateinit var solvedCheckBox: CheckBox
    private val crimeDetailViewModel:CrimeDetailViewModel by lazy{
        ViewModelProvider(this).get(CrimeDetailViewModel::class.java)
    }


    constructor(crimeId: UUID) : this() {
        val args=Bundle().apply{
            putSerializable(ARG_CRIME_ID,crimeId)
        }//Bundle 인자를 구성
       arguments=args
    }//bundle을 사용해서 저장할 경우 기본 생성자에서 bundle로 부터 arguments를 복원하는 과정이 존재하기 때문에 data가 유지될 수 있다.


    companion object{
        fun newInstance(crimeId:UUID):CrimeFragment{
            val args=Bundle().apply{
                putSerializable(ARG_CRIME_ID,crimeId)
            }//Bundle 인자를 구성
            return CrimeFragment().apply{
                arguments=args
            }// 구성한 Bundle 인자를 넣은 Fragment를 return
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crime = Crime()
        val crimeId:UUID=arguments?.getSerializable(ARG_CRIME_ID) as UUID //Bundle로부터 얻기
        crimeDetailViewModel.loadCrime(crimeId)
        Toast.makeText(context,"onCreate_crime",Toast.LENGTH_SHORT).show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crimeDetailViewModel.crimeLiveData?.observe(
            viewLifecycleOwner,
            Observer{ crime->
                crime?.let{
                    this.crime=crime
                    updateUI()
                }
            }
        )

    }
    private fun updateUI(){
        titleField.setText(crime.title)
        dateButton.text="date : "+crime.date.month+"월"+crime.date.date+"일 , "+(crime.date.year+1900)+"년"
        //solvedCheckBox.isChecked=crime.isSolved     - animation수행
        solvedCheckBox.apply{
            isChecked=crime.isSolved
            jumpDrawablesToCurrentState() // animation 비활성화
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime, container, false)

        titleField = view.findViewById(R.id.crime_title) as EditText
        dateButton = view.findViewById(R.id.crime_date) as Button
        solvedCheckBox = view.findViewById(R.id.crime_solved) as CheckBox
        timeButton=view.findViewById(R.id.crime_time) as Button


        /*dateButton.apply {
            text = crime.date.toString()
            isEnabled = false
        }
        기존에 date button에 날짜를 지정하기만 했던 방식*/

        return view
    }

    override fun onStart() {
        super.onStart()

        val titleWatcher = object : TextWatcher {
            override fun beforeTextChanged(
                sequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                // 여기서는 이 함수의 실행 코드를 구현할 필요가 없어서 비워 둔다
            }

            override fun onTextChanged(
                sequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                crime.title = sequence.toString()
            }

            override fun afterTextChanged(sequence: Editable?) {
                // 여기서는 이 함수의 실행 코드를 구현할 필요가 없어서 비워 둔다
            }
        }
        dateButton.setOnClickListener{
            DatePickerFragment.newInstance(crime.date).apply{

                setTargetFragment(this@CrimeFragment, REQUEST_DATE)
                //datepicker에서 crime fragment를 target으로 지정
                //startActivityforResult와 비슷한 역할을 한다고 보면 됨
                show(this@CrimeFragment.parentFragmentManager, DIALOG_DATE)
                //CrimeFragment내부에서 fragment manager를 호출하기 위해 사용한다. DatePickerFragment.apply에서는 this가 datepickerfragment이기 때문


            }
        }
        timeButton.setOnClickListener{

           TimePickerFragment.newInstance(crime.date).apply {
                setTargetFragment(this@CrimeFragment, REQUEST_TIME)
                show(this@CrimeFragment.parentFragmentManager, DIALOG_TIME)
            }


        }
        titleField.addTextChangedListener(titleWatcher)

        solvedCheckBox.apply {
            setOnCheckedChangeListener { _, isChecked ->
                crime.isSolved = isChecked
            }
        }
    }

    override fun onStop() {
        super.onStop()
        crimeDetailViewModel.saveCrime(crime)
    }

    override fun onDateSelected(date: Date) {
        crime.date=date
        updateUI()
    }
    override fun onTimeSelected(time:Date){

        val initial=crime.date
        initial.apply {
            hours=time.hours
            minutes=time.minutes
        }
        crime.date=initial
        updateUI()
    }
}