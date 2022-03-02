package org.techdown.nerdlauncher

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

private const val TAG="NerdLauncherActivity"

class NerdLauncherActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nerd_launcher)

        recyclerView=findViewById(R.id.app_recycler_view)
        recyclerView.layoutManager=LinearLayoutManager(this)
        //recyclerView 초기화




    }
    private fun setAdapter(){
        val startIntent=Intent(Intent.ACTION_MAIN).apply{
            addCategory(Intent.CATEGORY_LAUNCHER)

        }
        val activityies=packageManager.queryIntentActivities(startIntent,0)
        /*
        packageManager.queryIntentActivities(Intent, Int)를 호출하면 첫번째 인자로 전달한 Intent 와 일치하는 필터를 갖는 모든
        액티비티의 데이터(ResolveInfo 객체가 저장된 List)가 반환 된다. 두 번째 인자인 Int값은 flag이다.
        예를 들면 Package.GET_SHARED_LIBRARY_FILES 상수를 지정할 시 일치되는 각 앱과 연관된 라이브러리 파일들의 경로를 반환 결과에 포함할 수 있다.
        여기서는 0을 전달했으므로 결과가 변경되지 않는다.
         */
    }
}