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
import com.github.windsekirun.yukarisynthesizer.R
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
    val title = ObservableString()
    val max = ObservableInt()
    val progress = ObservableInt()
    val singleMode = ObservableBoolean()
    val playState = ObservableBoolean()
    val playTimeText = ObservableString()

    lateinit var disposable: Disposable

    private var playIndex: Int = 0
    private val targetList = mutableListOf<StoryItem>()
    private val mediaPlayer: MediaPlayer = MediaPlayer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.play_dialog)
        mBinding.dialog = this

        setOnDismissListener { stopTimer() }
    }

    fun show(list: List<StoryItem>) {
        super.show()

        targetList.clear()
        targetList.addAll(list)
        singleMode.set(targetList.size == 1)

        play(0)
    }

    fun clickSkipPrevious(view: View) {
        moveStory(false)
    }

    fun clickRewind(view: View) {
        moveSeconds(false)
    }

    fun clickPlay(view: View) {
        if (playState.get()) {
            mediaPlayer.pause()
            playState.set(false)
        } else {
            mediaPlayer.start()
            playState.set(true)
        }
    }

    fun clickForward(view: View) {
        moveSeconds(true)
    }

    fun clickSkipNext(view: View) {
        moveStory(true)
    }

    fun clickClose(view: View) {
        dismiss()
    }

    fun onStopTrackingTouch(seekBar: SeekBar) {
        val progress = seekBar.progress
        mediaPlayer.seekTo(progress * 1000)
        this.progress.set(progress)
    }

    private fun play(index: Int) {
        val storyItem = targetList[index]
        title.set(storyItem.title)
        val file = storyItem.localPath.toFile()

        if (mediaPlayer.isPlaying) mediaPlayer.stop()

        mediaPlayer.setOnCompletionListener {
            playState.set(false)
        }

        mediaPlayer.setDataSource(context, Uri.fromFile(file))
        mediaPlayer.prepare()
        mediaPlayer.start()

        playIndex = index
        playState.set(true)
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
        this.playTimeText.set("${duration.convertTime()}/${max.convertTime()}")
    }

    private fun stopTimer() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }

        if (!disposable.isDisposed) {
            disposable.dispose()
        }
    }

    private fun moveSeconds(forward: Boolean) {
        val currentPosition = mediaPlayer.currentPosition
        val duration = mediaPlayer.duration

        if (forward && currentPosition + seekForwardTime <= duration) {
            mediaPlayer.seekTo(currentPosition + seekForwardTime)
        } else if (currentPosition - seekBackwardTime > 0) {
            mediaPlayer.seekTo(currentPosition - seekBackwardTime)
        } else if (forward) {
            mediaPlayer.seekTo(duration)
        } else {
            mediaPlayer.seekTo(0)
        }
    }

    private fun moveStory(forward: Boolean) {
        if (playIndex == 0 && !forward) return
        if (playIndex == targetList.size - 1 && forward) return

        val newIndex = if (forward) ++playIndex else --playIndex
        play(newIndex)
    }

    private fun Int.convertTime() =
        "${(this / 60).toString().padStart(2, '0')}:${(this % 60).toString().padStart(2, '0')}"

    companion object {
        const val seekForwardTime = 5 * 1000
        const val seekBackwardTime = 5 * 1000
    }
}