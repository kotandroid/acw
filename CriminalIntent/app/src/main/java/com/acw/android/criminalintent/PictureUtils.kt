package com.acw.android.criminalintent

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point


    fun getScaledBitmap(path:String, destWidth:Int, destHeight:Int): Bitmap {
        var options=BitmapFactory.Options()
        options.inJustDecodeBounds=true
        //메모리에 이미지를 load하지않고 이미지의 width height정보만 가져올 수 있다.
        BitmapFactory.decodeFile(path,options)
        //이미지파일 읽기

        val srcWidth=options.outWidth.toFloat()
        val srcHeight=options.outHeight.toFloat()

        var inSampleSize=1
        if(srcHeight>destHeight||srcWidth>destWidth){
            //image 파일의 측정크기가 앱의 정해둔 크기보다 클 경우 크기를 조정한다.
            val heightScale=srcHeight/destHeight
            val widthScale=srcWidth/destWidth

            val sampleScale=if(heightScale>widthScale){
                heightScale
            }else{
                widthScale
            }
            inSampleSize=Math.round(sampleScale)
        }
        options=BitmapFactory.Options()
        options.inSampleSize=inSampleSize

        return BitmapFactory.decodeFile(path, options)
        /*
        inSampleSize가 화소를 결정하는데
        1이면 원래파일의 화소수에 1:1 대응대고
        2이면 원래파일의 화소수에 2:1 대응한다. 따라서 2일 경우에는 가로 1/2 세로 1/2로 총 1/4 크기를 갖게 된다.
         */
    }


    /*
     프래그먼트가 최초로 시작할 때는 photo view의 크기를 바로 알 수 없다. 레이아웃의 뷰 객체가 생성될 때 까지는 onCreate, onStart ... 등 여러 과정을 거치기 때문에 시간이 걸린다.
     따라서 레이아웃이 뷰 객체로 생성될 때 까지 기다리거나, Photo View의 크기가 어느 정도 될지 추정하는 방법을 사용해야한다.
     */


    //특정 activity의 화면 크기에 맞춰 Bitmap의 크기를 조정한다.
    fun getScaledBitmap(path: String,activity: Activity):Bitmap{
        val size= Point()

        @Suppress
        activity.windowManager.defaultDisplay.getSize(size)

        return getScaledBitmap(path,size.x,size.y)
    }


