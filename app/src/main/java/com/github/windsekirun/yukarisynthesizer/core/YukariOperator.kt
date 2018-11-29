package com.github.windsekirun.yukarisynthesizer.core

import com.github.windsekirun.yukarisynthesizer.MainApplication
import com.github.windsekirun.yukarisynthesizer.core.annotation.OrderType
import com.github.windsekirun.yukarisynthesizer.core.define.VoiceEngine
import com.github.windsekirun.yukarisynthesizer.core.item.*
import com.github.windsekirun.yukarisynthesizer.core.repository.PreferenceRepository
import com.github.windsekirun.yukarisynthesizer.core.repository.PreferenceRepositoryImpl
import com.github.windsekirun.yukarisynthesizer.core.test.sm30193805Test
import io.objectbox.Box
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
import pyxis.uzuki.live.richutilskt.utils.asDateString
import pyxis.uzuki.live.richutilskt.utils.toFile
import java.io.File
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class YukariOperator @Inject constructor(val application: MainApplication) {
    private val phonomeBox: Box<PhonomeItem> by lazy { application.getBox(PhonomeItem::class.java) }
    private val presetBox: Box<PresetItem> by lazy { application.getBox(PresetItem::class.java) }
    private val storyBox: Box<StoryItem> by lazy { application.getBox(StoryItem::class.java) }
    private val voiceBox: Box<VoiceItem> by lazy { application.getBox(VoiceItem::class.java) }
    private val preferenceRepository: PreferenceRepository by lazy { PreferenceRepositoryImpl(application) }

    /**
     * add [StoryItem] to box with update asscioated [StoryItem.voices]
     *
     * @param storyItem [StoryItem] to add
     * @return id of added row
     */
    fun addStoryItem(storyItem: StoryItem): Observable<Long> {
        return Observable.create {
            val id = storyBox.put(storyItem)
            it.onNext(id)
        }
    }

    fun generateTestData() {
        val data1 = sm30193805Test()
        storyBox.put(data1)
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
    fun getPresetList(
        page: Int = -1,
        limit: Long = 20L,
        searchTitle: String = "",
        orderBy: Pair<@OrderType Int, Property<PresetItem>> = OrderType.OrderFlags.ASCENDING to PresetItem_.regDate
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
     * @param localPlayable return list which contains valid localPath
     * @return searched list by given options.
     */
    fun getStoryList(
        page: Int = -1,
        limit: Long = 20L,
        searchTitle: String = "",
        orderBy: Pair<@OrderType Int, Property<StoryItem>> = OrderType.OrderFlags.ASCENDING to StoryItem_.regDate,
        localPlayable: Boolean = false
    ): Observable<List<StoryItem>> {
        return updateStoryItemPath()
            .flatMap {
                val notEqual = if (localPlayable) "" to StoryItem_.localPath else null

                val list = nativeQuerySearch(
                    storyBox, page, limit, searchTitle to StoryItem_.title, orderBy, notEqual
                ).map { item -> item.findMetadata() }

                Observable.just(list)
            }
    }

    /**
     * get List of [VoiceItem] by given options
     *
     * all parameter in [getVoiceList] are optional parameter.
     *
     * @param searchTitle return list by contains given value in [VoiceItem_.contentOrigin]. Default value is ""
     * @param orderBy return list by given order. Int will indicate one value of [OrderType.OrderFlags],
     * [Property] will indicate what order can applied.
     * Default value is [OrderType.OrderFlags.ASCENDING] to [VoiceItem_.regDate]
     * @return searched list by given options
     */
    fun getVoiceList(
        searchTitle: String = "",
        orderBy: Pair<@OrderType Int, Property<VoiceItem>> = OrderType.OrderFlags.ASCENDING to VoiceItem_.regDate
    ): Observable<List<VoiceItem>> {
        return Observable.create { emitter ->
            val list = nativeQuerySearch(voiceBox, -1, -1, searchTitle to VoiceItem_.contentOrigin,
                orderBy)

            emitter.onNext(list)
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
        val ffmpeg = FFmpeg.getInstance(application)
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

        val file = File(application.filesDir, "/local/$title.mp3")
        file.parentFile.mkdirs()

        this.localPath = file.absolutePath
        storyBox.put(this)

        return this
    }

    /**
     * find and apply 'transient metadata' with given [StoryItem]
     */
    private fun StoryItem.findMetadata() = this.apply {
        val majorEngine: VoiceEngine = voices.map { it.engine }
            .groupBy { it }
            .mapValues { it.value.size }
            .toList()
            .maxBy { it.second }
            ?.first ?: VoiceEngine.NONE

        this.regDateFormat = this.regDate.asDateString("yyyy. MM. dd.")
        this.majorEngine = majorEngine
    }

    /**
     * Run native query search by given options.
     */
    private fun <ENTITY> nativeQuerySearch(
        box: Box<ENTITY>,
        page: Int,
        limit: Long,
        searchTitle: Pair<String, Property<ENTITY>>,
        orderBy: Pair<@OrderType Int, Property<ENTITY>>,
        notEqual: Pair<String, Property<ENTITY>>? = null
    ): MutableList<ENTITY> {
        val query = box.query {
            if (searchTitle.first.isNotEmpty()) this.contains(searchTitle.second, searchTitle.first)
            this.order(orderBy.second, orderBy.first)
            if (notEqual != null) this.notEqual(notEqual.second, notEqual.first)
        }

        return if (page != -1 && limit != -1L) {
            val offset = (page - 1) * limit
            query.find(offset, limit)
        } else {
            query.find()
        }
    }

    /**
     * update unreadable [StoryItem_.localPath] to empty in all value of [storyBox]
     */
    private fun updateStoryItemPath(): Observable<Int> {
        return Observable.create { emitter ->
            val notEqual = "" to StoryItem_.localPath
            val orderBy = OrderType.OrderFlags.ASCENDING to StoryItem_.regDate

            // find local playable list
            val list = nativeQuerySearch(
                storyBox, -1, -1, "" to StoryItem_.title, orderBy, notEqual
            )

            val target = list.filter { it.localPath.isNotEmpty() }
                .filter {
                    val file = it.localPath.toFile()
                    !(file.exists() && file.canRead())
                }
                .map { it.apply { it.localPath = "" } }
                .toList()

            storyBox.put(target)
            emitter.onNext(target.size)
        }
    }

    companion object {
        const val DOCOMO_URL = "https://api.apigw.smt.docomo.ne.jp/aiTalk/v1/textToSpeech?APIKEY=%s"
        const val CONTENT_TYPE_SSML = "application/ssml+xml"
        const val MIME_TYPE_L16 = "audio/L16"
    }
}