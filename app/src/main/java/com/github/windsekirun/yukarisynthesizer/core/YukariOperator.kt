package com.github.windsekirun.yukarisynthesizer.core

import android.content.Context
import com.github.windsekirun.yukarisynthesizer.core.annotation.OrderType
import com.github.windsekirun.yukarisynthesizer.core.item.*
import com.github.windsekirun.yukarisynthesizer.core.repository.PreferenceRepository
import com.github.windsekirun.yukarisynthesizer.core.repository.PreferenceRepositoryImpl
import io.objectbox.Box
import io.objectbox.BoxStore
import io.objectbox.Property
import io.objectbox.kotlin.query
import io.reactivex.Observable
import io.reactivex.Single
import nl.bravobit.ffmpeg.ExecuteBinaryResponseHandler
import nl.bravobit.ffmpeg.FFmpeg
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okio.Okio
import java.io.File
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class YukariOperator @Inject constructor(val context: Context) {
    private val myObjectBox: BoxStore by lazy { MyObjectBox.builder().androidContext(context).build() }
    private val phonomeBox: Box<PhonomeItem> by lazy { myObjectBox.boxFor(PhonomeItem::class.java) }
    private val presetBox: Box<PresetItem> by lazy { myObjectBox.boxFor(PresetItem::class.java) }
    private val storyBox: Box<StoryItem> by lazy { myObjectBox.boxFor(StoryItem::class.java) }
    private val voiceBox: Box<VoiceItem> by lazy { myObjectBox.boxFor(VoiceItem::class.java) }
    private val preferenceRepository: PreferenceRepository by lazy { PreferenceRepositoryImpl(context) }

    /**
     * add [StoryItem] to box with update asscioated [StoryItem.voices]
     *
     * @param storyItem [StoryItem] to add
     * @return id of added row
     */
    fun addStoryItem(storyItem: StoryItem): Observable<Long> {
        return Observable.create {
            // update voice
            val voices = storyItem.voices
            voiceBox.put(voices)

            val id = storyBox.put(storyItem)
            it.onNext(id)
        }
    }

    /**
     * get List of [PhonomeItem] by given options.
     *
     * all parameter in [getPhonomeList] are optional parameter.
     *
     * @param searchTitle return list by contains given value in [PhonomeItem.title]. Default value is ""
     * @param recent return list with last 10 items of [PhonomeItem]
     * @return searched list by given options.
     */
    fun getPhonomeList(searchTitle: String = "", recent: Boolean = true): Observable<List<PhonomeItem>> {
        return Observable.create {
            val page = if (recent) 1 else -1
            val limit = if (recent) 10L else -1L
            val orderType = if (recent) OrderType.OrderFlags.DESCENDING else OrderType.OrderFlags.ASCENDING

            val list = nativeQuerySearch(
                    phonomeBox, page, limit, searchTitle to PhonomeItem_.origin,
                    orderType to PhonomeItem_.regDate
            )
            it.onNext(list)
        }
    }

    /**
     * get List of [PresetItem] by given options
     *
     * all parameter in [getPresetList] are optional parameter.
     *
     * @param searchTitle return list by contains given value in [PresetItem_.title]. Default value is ""
     * @param orderBy return list by given order. Int will indicate one value of [OrderType.OrderFlags],
     * [Property] will indicate what order can applied.
     * Default value is [OrderType.OrderFlags.ASCENDING] to [PresetItem_.regDate]
     * @param page return list by pagination. Pagination only available when page and limit aren't indicate -1.
     * This parameter starts from 1, not 0. Default value is -1 (not used)
     * @param limit return list by pagination. Default value is 20.
     * @return searched list by given options.
     */
    fun getPresetList(page: Int = -1,
                      limit: Long = 20L,
                      searchTitle: String = "",
                      orderBy: Pair<@OrderType Int, Property<PresetItem>> =
                              OrderType.OrderFlags.ASCENDING to PresetItem_.regDate
    ): Observable<List<PresetItem>> {
        return Observable.create {
            val list = nativeQuerySearch(presetBox, page, limit, searchTitle to PresetItem_.title, orderBy)
            it.onNext(list)
        }
    }

    /**
     * get List of [StoryItem] by given options
     *
     * all parameter in [getStoryList] are optional parameter.
     *
     * @param searchTitle return list by contains given value in [StoryItem_.title]. Default value is ""
     * @param orderBy return list by given order. Int will indicate one value of [OrderType.OrderFlags],
     * [Property] will indicate what order can applied.
     * Default value is [OrderType.OrderFlags.ASCENDING] to [StoryItem_.regDate]
     * @param page return list by pagination. Pagination only available when page and limit aren't indicate -1.
     * This parameter starts from 1, not 0. Default value is -1 (not used)
     * @param limit return list by pagination. Default value is 20.
     * @return searched list by given options.
     */
    fun getStoryList(page: Int = -1,
                     limit: Long = 20L,
                     searchTitle: String = "",
                     orderBy: Pair<@OrderType Int, Property<StoryItem>> =
                             OrderType.OrderFlags.ASCENDING to StoryItem_.regDate
    ): Observable<List<StoryItem>> {
        return Observable.create {
            val list = nativeQuerySearch(
                    storyBox, page, limit, searchTitle to StoryItem_.title,
                    orderBy
            )
            it.onNext(list)
        }
    }

    /**
     * request Synthesis with given [storyItem]
     *
     * @param storyItem [StoryItem] to synthesis
     */
    fun requestSynthesis(storyItem: StoryItem): Single<File> {
        val target = storyItem.addStoryLocalPath()
        val client = OkHttpClient()
        val ffmpeg = FFmpeg.getInstance(context)
        val url = DOCOMO_URL.format(preferenceRepository.apiKey)

        val pcmFile = File(target.localPath.replace("mp3", "pcm"))
        val mp3File = File(target.localPath)
        val ssml = SSMLBuilder.process(target, true)
        val body = RequestBody.create(MediaType.parse(CONTENT_TYPE_SSML), ssml)

        val ffmpegCommend = arrayOf(
                "-y", "-ac", "1", "-ar", "16000", "-f", "s16be", "-i", pcmFile.absolutePath,
                "-c:a", "libmp3lame", "-q:a", "2", mp3File.absolutePath
        )

        val request = Request.Builder().url(url)
                .addHeader("Content-Type", CONTENT_TYPE_SSML)
                .addHeader("Accept", MIME_TYPE_L16)
                .post(body)
                .build()
        if (!ffmpeg.isSupported) {
            return Single.error(UnsupportedOperationException("Doesn't not support FFmpeg"))
        }

        return Single.fromCallable { client.newCall(request).execute() }
                .flatMap {
                    if (it.isSuccessful && it.body() != null) {
                        Single.just(it)
                    } else {
                        Single.error(IOException("Cannot fetch from Docomo API"))
                    }
                }.flatMap {
                    Single.just(Okio.buffer(Okio.sink(pcmFile)).use { sink ->
                        sink.writeAll(it.body()!!.source())
                        return@use pcmFile
                    })
                }.flatMap {
                    Single.create<File> { emitter ->
                        ffmpeg.execute(ffmpegCommend, object : ExecuteBinaryResponseHandler() {
                            override fun onSuccess(message: String?) {
                                super.onSuccess(message)
                                emitter.onSuccess(mp3File)

                            }

                            override fun onFailure(message: String?) {
                                super.onFailure(message)
                                emitter.onError(IOException("Cannot convert by FFmpeg"))
                            }
                        })
                    }
                }
    }

    /**
     * generate [StoryItem.localPath] if [StoryItem.localPath] isn't available
     */
    private fun StoryItem.addStoryLocalPath(): StoryItem {
        if (localPath.isNotEmpty()) return this

        val file = File(context.filesDir, "/local/$title.mp3")
        file.parentFile.mkdirs()

        this.localPath = file.absolutePath
        storyBox.put(this)

        return this
    }

    /**
     * Run native query search by given options.
     */
    private fun <ENTITY> nativeQuerySearch(box: Box<ENTITY>,
                                           page: Int,
                                           limit: Long,
                                           searchTitle: Pair<String, Property<ENTITY>>,
                                           orderBy: Pair<@OrderType Int, Property<ENTITY>>
    ): MutableList<ENTITY> {
        val query = box.query {
            if (searchTitle.first.isNotEmpty()) this.contains(searchTitle.second, searchTitle.first)
            this.order(orderBy.second, orderBy.first)
        }

        return if (page != -1 && limit != -1L) {
            val offset = (page - 1) * limit
            query.find(offset, limit)
        } else {
            query.find()
        }
    }

    companion object {
        const val DOCOMO_URL = "https://api.apigw.smt.docomo.ne.jp/aiTalk/v1/textToSpeech?APIKEY=%s"
        const val CONTENT_TYPE_SSML = "application/ssml+xml"
        const val MIME_TYPE_L16 = "audio/L16"
    }
}