package com.acw.android.criminalintent

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.util.*



private const val ARG_TIME="time_picker"


class TimePickerFragment :DialogFragment(){

    interface Callbacks{
        fun onTimeSelected(time:Date)
    }


    override fun onCreateDialog(savedInstanceState : Bundle?):Dialog{
        Log.d(ARG_TIME,"check")

        val timeListener= TimePickerDialog.OnTimeSetListener{
                view: TimePicker, hourOfDay:Int, minute:Int->
            //_ 는 datepicker 객체이며 여기서는 사용하지 않으므로 _를 사용했다.
            //생략된 매개변수를 사용할 때 _를 사용한다.

                val resultTime= Date().apply {
                    hours = hourOfDay
                    minutes = minute
                }

            targetFragment?.let{
                fragment -> (fragment as Callbacks).onTimeSelected(resultTime)
            }

        }
        val time=arguments?.getSerializable(ARG_TIME) as Date
        val calendar= Calendar.getInstance()
        calendar.time=time
        val initialHour=calendar.get(Calendar.HOUR_OF_DAY)
        val initialMinute=calendar.get(Calendar.MINUTE)
        return TimePickerDialog(
            context,
            timeListener,
            initialHour,
            initialMinute,
            false
        )
    }

    companion object{
        fun newInstance(date:Date):TimePickerFragment{
            val args=Bundle().apply{
                putSerializable(ARG_TIME,date)
            }

            return TimePickerFragment().apply{
                arguments=args
            }
        }
    }

}