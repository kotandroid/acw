package com.acw.android.criminalintent

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.text.format.DateFormat.format
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.*
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.Observer
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.hours
import kotlin.time.toDuration

private const val DIALOG_DATE="DialogDate" //dialog를 식별하기 위한 인자
private const val DIALOG_TIME="DialogTime"
private const val PHOTO_PATH="photo_path"
private const val REQUEST_DATE=0
private const val REQUEST_TIME=1
private const val REQUEST_CONTACT_NAME=2
private const val REQUEST_PHOTO=3

private const val ARG_CRIME_ID="crime_id"
private const val DATE_FORMAT="yyyy년 M월 d일 H시 m분, E요일"


class CrimeFragment() : Fragment(),DatePickerFragment.Callbacks,TimePickerFragment.Callbacks {

    private lateinit var crime: Crime
    private lateinit var titleField: EditText
    private lateinit var dateButton: Button
    private lateinit var timeButton: Button
    private lateinit var solvedCheckBox: CheckBox
    private lateinit var reportbutton:Button
    private lateinit var suspectButton:Button
    private lateinit var callButton:Button
    private lateinit var callIntent:Intent
    private lateinit var photoButton:ImageButton
    private lateinit var photoView:ImageView
    private lateinit var photoFile:File
    private lateinit var photoUri:Uri
    private lateinit var title_area:LinearLayout
    private var callbacks: CrimeFragment.Callbacks?=null
    private  var width_photo=0
    private  var height_photo=0
    private val crimeDetailViewModel:CrimeDetailViewModel by lazy{
        ViewModelProvider(this).get(CrimeDetailViewModel::class.java)
    }

    interface Callbacks{
        fun onPhotoSelected(photoFile:File)
    }
    constructor(crimeId: UUID) : this() {
        val args=Bundle().apply{
            putSerializable(ARG_CRIME_ID,crimeId)
        }//Bundle 인자를 구성
       arguments=args
    }//bundle을 사용해서 저장할 경우 기본 생성자에서 bundle로 부터 arguments를 복원하는 과정이 존재하기 때문에 data가 유지될 수 있다.
    // 책에서 언급한 기본 생성자 사용은 bundle을 이용하는 것이 아닌 인자로 데이터를 넘겨 초기화하는 방법을 말하는 것이다.


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


    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks=context as CrimeFragment.Callbacks?
        //activity의 context를 callback에 할당
        //activity는 context의 서브 클래스이다. 따라서 인자로 activity를 전달해도 무방하다.(but activity를 전달하는 onAttach는 향후 버전에서 deprecated될 확률있음)
        //context를 Callbacks type으로 할당하여 반드시 Callbacks interface를 구현하도록 했다.
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
                    photoFile=crimeDetailViewModel.getPhotoFile(crime)
                    photoUri=FileProvider.getUriForFile(requireActivity(),"com.acw.android.criminalintent.fileprovider",photoFile)
                    //photoFile은 file의 경로이고, 이를 이용해 카메라가 이해할 수 있게 경로를 URI로 변경한다.
                    updateUI()
                }
            }
        )
        photoView.viewTreeObserver.addOnGlobalLayoutListener (object :ViewTreeObserver.OnGlobalLayoutListener{
            override fun onGlobalLayout() {
                width_photo=photoView.width
                height_photo=photoView.height
            }

        } )

    }
    private fun updatePhotoView(){
        if(photoFile.exists()){
            val bitmap=getScaledBitmap(photoFile.path,requireActivity())

            photoView.setImageBitmap(bitmap)
        }
        else{
            photoView.setImageDrawable(null)
        }
    }
    private fun updateUI(){
        titleField.setText(crime.title)
        dateButton.text="date : "+crime.date.month+"월"+crime.date.date+"일 , "+(crime.date.year+1900)+"년"
        //solvedCheckBox.isChecked=crime.isSolved     - animation수행
        timeButton.text="time: "+crime.date.hours+"시 "+crime.date.minutes+"분"
        solvedCheckBox.apply{
            isChecked=crime.isSolved
            jumpDrawablesToCurrentState() // animation 비활성화
        }
        if(crime.suspect.isNotEmpty()){
            suspectButton.text=crime.suspect
        }
        updatePhotoView()

    }
    private fun getCrimeReport():String{
        val solvedString=if(crime.isSolved){
            getString(R.string.crime_solved)
        }else{
            getString(R.string.crime_unsolved)
        }

        val dateString= DateFormat.format(DATE_FORMAT,crime.date).toString()
        var suspect=if(crime.suspect.isBlank()){
            getString(R.string.crime_report_no_suspect)
        }else{
            getString(R.string.crime_report_suspect,crime.suspect)
        }

        return getString(R.string.crime_report,crime.title,dateString,solvedString,suspect)
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
        reportbutton=view.findViewById(R.id.crime_report) as Button
        suspectButton=view.findViewById(R.id.crime_suspect) as Button
        callButton=view.findViewById(R.id.crime_call) as Button
        photoButton=view.findViewById(R.id.crime_camera) as ImageButton
        photoView=view.findViewById(R.id.crime_photo) as ImageView

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
        //날짜 버튼 설정
        dateButton.setOnClickListener {
            DatePickerFragment.newInstance(crime.date).apply {

                setTargetFragment(this@CrimeFragment, REQUEST_DATE)
                //datepicker에서 crime fragment를 target으로 지정
                //startActivityforResult와 비슷한 역할을 한다고 보면 됨
                show(this@CrimeFragment.parentFragmentManager, DIALOG_DATE)
                //CrimeFragment내부에서 fragment manager를 호출하기 위해 사용한다. DatePickerFragment.apply에서는 this가 datepickerfragment이기 때문


            }
        }
        //시간 버튼 설정
        timeButton.setOnClickListener {

            TimePickerFragment.newInstance(crime.date).apply {
                setTargetFragment(this@CrimeFragment, REQUEST_TIME)
                show(this@CrimeFragment.parentFragmentManager, DIALOG_TIME)
            }


        }
        //보고서 전송버튼 설정
        reportbutton.setOnClickListener {
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getCrimeReport()) // 내용
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject))

            }.also { intent ->
                val chooserIntent = Intent.createChooser(intent, getString(R.string.send_report))
                startActivity(chooserIntent)
            }
        }

        //용의자 선택 버튼 설정
        suspectButton.apply {
            val pickContactIntent =
                Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
            //연락처에서 사람을 선택하여 activityresult에서 intent로 선택한 사람의 data를 받아 처리한다.
            setOnClickListener {
                startActivityForResult(pickContactIntent, REQUEST_CONTACT_NAME)
            }



            //만약 연락처 어플이 없을 경우 앱이 중단되기 때문에 없을 경우 버튼 누르기를 비활성화 하도록 한다.
            val packageManager: PackageManager = requireActivity().packageManager //안드로이드 운영체제의 일부
            val resolvedActivity: ResolveInfo? =
                packageManager.resolveActivity(pickContactIntent, PackageManager.MATCH_DEFAULT_ONLY)
            if (resolvedActivity == null) {
                isEnabled = false
            }
        }
        titleField.addTextChangedListener(titleWatcher)

        solvedCheckBox.apply {
            setOnCheckedChangeListener { _, isChecked ->
                crime.isSolved = isChecked
            }
        }
        callButton.apply {
            if (crime.suspect == null) {
                isEnabled = false
                text = "용의자 없음"
            } else {

                setOnClickListener {
                    startActivity(callIntent)
                }
            }
        }
        photoView.apply{
            setOnClickListener{
                callbacks?.onPhotoSelected(photoFile)
            }
        }
        photoButton.apply{
            val packageManager:PackageManager=requireActivity().packageManager
            //packagemanager는 안드로이드에 설치된 모든 component와 activity의 정보를 알고 있다.
            val captureImage=Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val resolvedActivity:ResolveInfo?=
                packageManager.resolveActivity(captureImage,PackageManager.MATCH_DEFAULT_ONLY)// resolveActivity(Intent,int)
            //resolve activity를 사용해, captureImage인텐트에 맞는 행동을 해줄 액티비티를 찾고, 플래그로도 제한을 걸 수 있다.
            // 여기서는 MATCH_DEFAULT_ONLY로 category default가 매니페스트 인텐트 필터에 정의된 액티비티만 검색한다.
            //startActivity(Intent)와 동일하다.

            if(resolvedActivity==null){
                isEnabled=false
            }

            setOnClickListener{
                Toast.makeText(context,"ss",Toast.LENGTH_SHORT).show()
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT,photoUri)
                val cameraActivities:List<ResolveInfo> = packageManager.queryIntentActivities(captureImage,PackageManager.MATCH_DEFAULT_ONLY)

                for(cameraActivity in cameraActivities){
                    requireActivity().grantUriPermission(
                        cameraActivity.activityInfo.packageName,photoUri,Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    //photo uri가 가리키는 위치에 사진 파일을 쓰려면 카메라 앱 퍼미션이 필요하다
                    )
                }
                startActivityForResult(captureImage, REQUEST_PHOTO)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when{
            resultCode!= Activity.RESULT_OK -> return
            requestCode== REQUEST_CONTACT_NAME && data !=null ->{

                val contactUri: Uri =data.data?:return
                //콘텐츠 제공자의 table 담고 있다.


                val queryField_name=arrayOf(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val queryField_num=arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)


                val cursor_name= requireActivity().contentResolver.query(
                    contactUri,// uri
                    queryField_name,// projection : The columns to return for each row
                    null,
                    null,
                    null
                )
                val cursor_num= requireActivity().contentResolver.query(
                    contactUri,
                    queryField_num,
                    null,
                    null,
                    null
                )


                cursor_name?.use{
                    if(it.count==0){
                        return
                    }// 용의자가 없으면 return
                    it.moveToFirst()// 있을 경우 첫 행이 용의자에 대한 data
                    val suspect_name=it.getString(0) // 첫행의 첫번째 열의 data
                    crime.suspect=suspect_name
                    crimeDetailViewModel.saveCrime(crime)
                    suspectButton.text=suspect_name
                    callButton.text=getString(R.string.crime_call,suspect_name)
                }
                cursor_num?.use{
                    if(it.count==0){
                        return
                    }
                    it.moveToFirst()
                    val suspect_num=it.getString(0)
                    callIntent=Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+suspect_num))
                   Toast.makeText(context,suspect_num,Toast.LENGTH_SHORT).show()
                }

            }
            requestCode== REQUEST_PHOTO ->{
                requireActivity().revokeUriPermission(photoUri,Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                //앞서 카메라에서 photoUri에 파일을 쓰기 위해 권한을 부여했었는데 쓰고나서 권한 회수를 한다.
                updatePhotoView()
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        requireActivity().revokeUriPermission(photoUri,Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
    }
}