package com.github.windsekirun.yukarisynthesizer.dialog

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import com.github.windsekirun.baseapp.base.BaseDialog
import com.github.windsekirun.bindadapters.observable.ObservableString
import com.github.windsekirun.yukarisynthesizer.core.item.StoryItem
import com.github.windsekirun.yukarisynthesizer.databinding.PlayDialogBinding
import com.github.windsekirun.yukarisynthesizer.utils.subscribe
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import pyxis.uzuki.live.richutilskt.utils.toFile
import java.util.concurrent.TimeUnit


/**
 * DokodemoYukariAndroidClient
 * Class: PlayDialog
 * Created by Pyxis on 12/1/18.
 *
 *
 * Description:
 */

class PlayDialog(context: Context) : BaseDialog<PlayDialogBinding>(context) {
    val targetList = mutableListOf<StoryItem>()
    val title = ObservableString()
    val max = ObservableInt()
    val progress = ObservableInt()
    val singleMode = ObservableBoolean()

    lateinit var disposable: Disposable

    private val mediaPlayer: MediaPlayer = MediaPlayer()

    override fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.play_dialog)
        mBinding.setDialog(this)

        setOnDismissListener { stopTimer() }
    }

    fun show(list: List<StoryItem>) {
        super.show()

        targetList.clear()
        targetList.addAll(list)
        singleMode.set(targetList.size == 1)

        play(targetList.first())
    }

    fun clickSkipPrevious(view: View) {

    }

    fun clickRewind(view: View) {
        val currentPosition = mediaPlayer.currentPosition
        if (currentPosition + seekForwardTime <= mediaPlayer.duration) {
            mediaPlayer.seekTo(currentPosition + seekForwardTime)
        } else {
            mediaPlayer.seekTo(mediaPlayer.duration)
        }
    }

    fun clickPlay(view: View) {

    }

    fun clickForward(view: View) {
        val currentPosition = mediaPlayer.currentPosition
        if (currentPosition + seekForwardTime <= mediaPlayer.duration) {
            mediaPlayer.seekTo(currentPosition + seekForwardTime)
        } else {
            mediaPlayer.seekTo(mediaPlayer.duration)
        }
    }

    fun clickSkipNext(view: View) {

    }

    fun clickClose(view: View) {

    }

    fun onStopTrackingTouch(seekBar: SeekBar) {
        val progress = seekBar.progress
        mediaPlayer.seekTo(progress * 1000)
        this.progress.set(progress)
    }

    private fun play(storyItem: StoryItem) {
        title.set(storyItem.title)
        val file = storyItem.localPath.toFile()

        mediaPlayer.setDataSource(context, Uri.fromFile(file))
        mediaPlayer.prepare()
        mediaPlayer.start()

        startTimer()
    }

    private fun startTimer() {
        disposable = Observable.interval(500, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { _, _ ->
                updateUI()
            }
    }

    private fun updateUI() {
        val duration = mediaPlayer.currentPosition / 1000
        val max = mediaPlayer.duration / 1000

        this.max.set(max)
        this.progress.set(duration)
    }

    private fun stopTimer() {
        if (!disposable.isDisposed) {
            disposable.dispose()
        }
    }

    companion object {
        const val seekForwardTime = 5 * 1000
        const val seekBackwardTime = 5 * 1000
    }
}