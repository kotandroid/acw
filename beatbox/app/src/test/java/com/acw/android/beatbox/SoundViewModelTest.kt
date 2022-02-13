package com.acw.android.beatbox

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import javax.security.auth.Subject

class SoundViewModelTest {
    private lateinit var beatBox:BeatBox
    private lateinit var sound:Sound
    private lateinit var subject: SoundViewModel


    @Before
    fun setUp() {
        beatBox=mock(BeatBox::class.java)
        sound=Sound("assetPath")
        subject= SoundViewModel(beatBox)
        subject.sound=sound

    }
    @Test
    fun expressSoundNameAsTitle(){
        MatcherAssert.assertThat(subject.title,`is`(sound.name))
        //test대상의 title 속성값이 sound.name과 같아야  test 통과
    }
    @Test
    fun callsBeatBoxPlayOnButtonClicked(){
        subject.onButtonClicked()
        verify(beatBox).play(sound)
    }
}