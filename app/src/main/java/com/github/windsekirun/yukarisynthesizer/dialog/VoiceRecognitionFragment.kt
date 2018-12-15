package com.github.windsekirun.yukarisynthesizer.dialog

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.appcompat.view.ContextThemeWrapper
import com.github.windsekirun.baseapp.utils.propertyChanges
import com.github.windsekirun.baseapp.utils.subscribe
import com.github.windsekirun.bindadapters.observable.ObservableString
import com.github.windsekirun.yukarisynthesizer.R
import com.github.windsekirun.yukarisynthesizer.databinding.VoiceRecognitionFragmentBinding
import com.github.windsekirun.yukarisynthesizer.module.sheet.RoundedBottomSheetDialogFragment
import io.reactivex.rxkotlin.addTo
import pyxis.uzuki.live.richutilskt.utils.selector


class VoiceRecognitionFragment : RoundedBottomSheetDialogFragment<VoiceRecognitionFragmentBinding>(),
    RecognitionListener {
    val partialResultText = ObservableString()

    lateinit var callback: (String) -> Unit

    private val speechRecognizer: SpeechRecognizer by lazy { SpeechRecognizer.createSpeechRecognizer(context) }
    private val results = mutableListOf<String>()

    override fun createView(inflater: LayoutInflater, container: ViewGroup?) =
        VoiceRecognitionFragmentBinding.inflate(inflater, container, false)

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fragment = this
        speechRecognizer.setRecognitionListener(this)

        binding.imgMic.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_UP -> {
                    stopRecognize()

                }

                MotionEvent.ACTION_DOWN -> {
                    startRecognize()
                    binding.rippleBackground.startRippleAnimation()
                }
            }

            false
        }
    }

    override fun onReadyForSpeech(params: Bundle?) {
        Log.d(TAG, "onReadyForSpeech")
    }

    override fun onRmsChanged(rmsdB: Float) {

    }

    override fun onBufferReceived(buffer: ByteArray?) {

    }

    override fun onPartialResults(partialResults: Bundle?) {
        val partialResultList = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        if (partialResultList != null && partialResultList.isNotEmpty()) {
            partialResultText.set(partialResultList[0])
        }
    }

    override fun onEvent(eventType: Int, params: Bundle?) {

    }

    override fun onBeginningOfSpeech() {
        Log.d(TAG, "onBeginningOfSpeech")
    }

    override fun onEndOfSpeech() {
        Log.d(TAG, "onEndOfSpeech")
        binding.rippleBackground.stopRippleAnimation()
    }

    override fun onError(error: Int) {
        Log.d(TAG, "onError")
    }

    override fun onResults(resultBundle: Bundle?) {
        val matches = resultBundle?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        if (matches != null && matches.isNotEmpty()) {
            results.clear()
            results.addAll(matches)
        }
    }

    fun clickSave(view: View) {
        context?.selector(results, { _, item, _ ->
            callback.invoke(item)
            dismiss()
        }, getString(R.string.select_nearest_result))
    }

    private fun startRecognize() {
        val speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            .apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, LANGUAGE_PREF)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, LANGUAGE_PREF)
                putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, LANGUAGE_PREF)
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
            }

        speechRecognizer.startListening(speechIntent)
    }

    private fun stopRecognize() {
        speechRecognizer.stopListening()
    }

    companion object {
        private val TAG = VoiceRecognitionFragment::class.java.simpleName
        private const val LANGUAGE_PREF = "ja"
    }
}