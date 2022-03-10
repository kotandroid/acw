package org.techdown.draganddraw

import android.content.Context
import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.widget.ConstraintSet
import kotlin.math.acos
import kotlin.math.pow
import kotlin.math.sqrt

private const val TAG="BoxDrawingView"




class BoxDrawingView(context: Context, attrs: AttributeSet?=null): View(context,attrs) {
    init{
        isEnabled=true
        isSaveEnabled=true
    }
    private var currentBox:Box?=null
    private var boxen=mutableListOf<Box>()
    private var boxPaint= Paint().apply{
        color=0x22ff00ee.toInt()
    }
    private val backgroundPaint=Paint().apply{
        color=0xfff8efe0.toInt()
    }

    override fun onDraw(canvas: Canvas) {
        Log.d(TAG,"onDraw")
        canvas.drawPaint(backgroundPaint)

        boxen.forEach{
            box->canvas.drawRect(box.left,box.top,box.right,box.bottom,boxPaint)
        }
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        val current= PointF(event.x,event.y)
        var action=""


        when (event.action){
            MotionEvent.ACTION_DOWN->{
                action="ACTION_DOWN"
                currentBox=Box(current).also{
                    boxen.add(it)
                }
            }
            MotionEvent.ACTION_MOVE->{
                action="ACTION_MOVE"
                updateCurrentBox(current)

            }
            MotionEvent.ACTION_UP->{
                action="ACTION_UP"
                updateCurrentBox(current)
                currentBox=null
            }
            MotionEvent.ACTION_CANCEL->{
                action="ACTION_CANCEL"
                currentBox=null
            }
        }
        Log.i(TAG,"$action at x=${current.x} y=${current.y}")

        return true
    }

    private fun updateCurrentBox(current:PointF){
        currentBox?.let{
            it.end=current
            invalidate()
        }
    }


    override fun onSaveInstanceState(): Parcelable? {
        Log.d(TAG,"onSaved")
          return Bundle().apply{
              val boxList:List<Parcelable> = boxen.toList()
              val boxarr=ArrayList<Parcelable>()
              boxarr.addAll(boxList)

              putParcelable("super",super.onSaveInstanceState())
              putParcelableArrayList("Boxes",boxarr)
          }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        Log.d(TAG,"onRestoreInstanceState")

         if(state is Bundle){
             super.onRestoreInstanceState(state.getParcelable("super"))

             val boxarr=state.getParcelableArrayList<Parcelable>("Boxes")
             val boxList=boxarr?.toList()
             boxen=boxList?.toMutableList() as MutableList<Box>
         }

        else{
             Log.d(TAG,"bundle is empty")
         }


    }
    fun calradius(prevX1:Int,prevX2:Int,prevY1:Int,prevY2:Int,nextX1:Int,nextX2:Int,
                  nextY1:Int,nextY2:Int) :Double{
        val prevdist=caldist(prevX1,prevX2,prevY1,prevY2)/2
        val nextdist=caldist(nextX1,nextX2,nextY1,nextY2)/2
        val otherdist=caldist(prevX2,nextX2,prevY2,nextY2)

        return acos((prevdist.pow(2)+nextdist.pow(2)-otherdist.pow(2)
                /(2*prevdist*nextdist)))
    }


    fun caldist(x1:Int,x2:Int,y1:Int,y2:Int) :Double{
        var bx=0
        var by=0
        var mx=0
        var my=0

        if(x1>x2){
            bx=x1
            mx=x2
        }else{
            bx=x2
            mx=x1
        }

        if(y1>y2){
            by=y1
            my=y2
        }else{
            by=y2
            by=y1
        }

        return sqrt(((bx-mx).toDouble().pow(2)+(by-my).toDouble().pow(2)))

    }


}