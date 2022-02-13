package com.acw.android.beatbox

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.acw.android.beatbox.databinding.ActivityMainBinding
import com.acw.android.beatbox.databinding.ListItemSoundBinding

class MainActivity : AppCompatActivity() {



    private val mainViewModel :MainViewModel by lazy{
        ViewModelProvider(this).get(MainViewModel::class.java)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        mainViewModel.beatBox= BeatBox(assets)// BeatBox에 asset폴더 전달

        val binding :ActivityMainBinding=
            DataBindingUtil.setContentView(this,R.layout.activity_main)
        //데이터 바인딩 클래스를 inflate

        binding.recyclerView.apply{
            layoutManager=GridLayoutManager(context,3)
            adapter=SoundAdapter(mainViewModel.beatBox.sounds)
        }
        binding.seekBar2.setOnSeekBarChangeListener(object:SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                binding.textSeekbar.text=p1.toString()
                mainViewModel.beatBox.speed=p1.toFloat()
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }
        })
    }


    private inner class SoundHolder(private val binding:ListItemSoundBinding): RecyclerView.ViewHolder(binding.root){
        init{
            binding.viewModel= SoundViewModel(mainViewModel.beatBox)
            //view model 인스턴스를 생성하고 바인딩클래스의 viewModel속성을 초기화한다.
        }
        fun bind(sound:Sound){
            //view model 속성을 변경한다.
            binding.apply{
                viewModel?.sound=sound
                executePendingBindings()
                //보통은 executePendingBindings를 호출할 필요가 없지만 현재 앱에서는 RecyclerView에 포함된 바인딩 데이터를 빠르게 변경해야 하기 때문에 사용했다.
            }
        }
    }


    private inner class SoundAdapter(private val sounds:List<Sound>):RecyclerView.Adapter<SoundHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SoundHolder {

            val binding=DataBindingUtil.inflate<ListItemSoundBinding>(
                layoutInflater,
                R.layout.list_item_sound,
                parent,
                false
            )
            return SoundHolder(binding)
        }

        override fun onBindViewHolder(holder: SoundHolder, position: Int) {
            val sound=sounds[position]
            holder.bind(sound)
        }

        override fun getItemCount(): Int {
            return sounds.size
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mainViewModel.beatBox.release()
    }
}