package com.github.windsekirun.yukarisynthesizer.core

import com.annimon.stream.ComparatorCompat
import com.github.windsekirun.yukarisynthesizer.MainApplication
import com.github.windsekirun.yukarisynthesizer.core.annotation.OrderType
import com.github.windsekirun.yukarisynthesizer.core.define.VoiceEngine
import com.github.windsekirun.yukarisynthesizer.core.item.*
import com.github.windsekirun.yukarisynthesizer.core.repository.PreferenceRepository
import com.github.windsekirun.yukarisynthesizer.core.repository.PreferenceRepositoryImpl
import com.github.windsekirun.yukarisynthesizer.core.utils.YukariUtils
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
import okio.BufferedSource
import okio.Okio
import pyxis.uzuki.live.richutilskt.utils.asDateString
import pyxis.uzuki.live.richutilskt.utils.toFile
import java.io.File
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Operator class for handle business logic which used in ViewModel
 */
@Suppress("UNUSED_EXPRESSION")
@Singleton
class YukariOperator @Inject constructor(val application: MainApplication) {
    private val phonomeBox: Box<PhonomeItem> by lazy { application.getBox(PhonomeItem::class.java) }
    private val presetBox: Box<PresetItem> by lazy { application.getBox(PresetItem::class.java) }
    private val storyBox: Box<StoryItem> by lazy { application.getBox(StoryItem::class.java) }
    private val voiceBox: Box<VoiceItem> by lazy { application.getBox(VoiceItem::class.java) }
    private val preferenceRepository: PreferenceRepository by lazy { PreferenceRepositoryImpl(application) }

    /**
     * add List of [PhonomeItem] to box
     *
     * @param list [PhonomeItem] to add
     * @return List of id to store in [VoiceItem]
     */
    fun addPhonomeItems(list: List<PhonomeItem>): Observable<List<Long>> {
        return Observable.create { emitter ->
            val target = mutableListOf<PhonomeItem>().apply { addAll(list) }
            phonomeBox.put(target)
            val ids = target.map { it.id }
            emitter.onNext(ids)
        }
    }

    /**
     * add [PresetItem] into box
     *
     * @param presetItem [PresetItem] to add
     * @return id of added row
     */
    fun addPresetItem(presetItem: PresetItem): Observable<Long> {
        return Observable.create { emitter ->
            presetBox.put(presetItem)
            emitter.onNext(presetItem.id)
        }
    }

    /**
     * add [StoryItem] to box with update associated [StoryItem.voiceEntries]
     *
     * @param storyItem [StoryItem] to add
     * @param simpleChange doesn't execute saving associated VoiceItem
     * @return id of added row
     */
    fun addStoryItem(storyItem: StoryItem, simpleChange: Boolean = false): Observable<Long> {
        return Observable.create { emitter ->
            // save associated VoiceItem
            if (!simpleChange) {
                val list = storyItem.voiceEntries.toList()
                voiceBox.put(list)

                val ids = list.map { it.id }

                storyItem.apply {
                    this.voicesIds = ids
                    this.majorEngine = YukariUtils.findMajorEngine(this)
                }
            }

            val id = storyBox.put(storyItem)
            emitter.onNext(id)
        }
    }

    /**
     * add [VoiceItem] to box with update associated [VoiceItem.phonomes]
     *
     * @param voiceItem [VoiceItem] to add
     * @return added row
     */
    fun addVoiceItem(voiceItem: VoiceItem): Observable<VoiceItem> {
        return Observable.create { emitter ->
            val list = voiceItem.phonomes.toList()
            phonomeBox.put(list)

            val ids = list.map { it.id }

            voiceItem.apply {
                this.phonomeIds = ids
                bindContentOrigin()
            }

            voiceBox.put(voiceItem)
            emitter.onNext(voiceItem)
        }
    }

    /**
     * execute First run for new comer!
     */
    fun firstRunSetup(): Observable<Boolean> {
        return Observable.create { emitter ->
            if (!preferenceRepository.newUser) {
                emitter.onNext(true)
                return@create
            }

            preferenceRepository.newUser = false
            val yukariPreset = getInternalPresetItem(VoiceEngine.Yukari)
            val makiPreset = getInternalPresetItem(VoiceEngine.Maki)

            presetBox.put(yukariPreset, makiPreset)

            emitter.onNext(true)
        }
    }

    /**
     * get Default [PresetItem] with given [VoiceEngine] to add [VoiceItem] with voice recognition feature.
     *
     * @param engine [VoiceEngine] to find
     * @return [PresetItem]
     */
    fun getDefaultPresetItem(engine: VoiceEngine): Observable<PresetItem> {
        return getPresetList()
            .flatMap { list ->
                val presetItem: PresetItem = if (list.isNotEmpty()) {
                    list.firstOrNull { it.default && it.engine == engine } ?: getInternalPresetItem(engine)
                } else {
                    getInternalPresetItem(engine)
                }

                if (presetItem.id == 0L) presetBox.put(presetItem)
                Observable.just(presetItem)
            }
    }

    /**
     * get List of [PhonomeItem] by given options.
     *
     * all parameter in [getPhonomeList] are optional parameter.
     *
     * @param searchTitle return list by contains given value in [PhonomeItem.origin]. Default value is ""
     * @param recent return list with last 10 items of [PhonomeItem]. if false, orderby [OrderType.OrderFlags.ASCENDING]
     * to [PhonomeItem_.origin]
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
     * get List of [PhonomeItem] which associated with given [VoiceItem]
     *
     * @param voiceItem associated data
     * @return searched list by given [voiceItem]
     */
    fun getPhonomeListAssociatedVoiceItem(voiceItem: VoiceItem): Observable<List<PhonomeItem>> {
        return Observable.create { emitter ->
            val ids = voiceItem.phonomeIds.toLongArray()

            // kotlin-ported version of https://stackoverflow.com/a/45699250
            val query = phonomeBox.query { this.`in`(PhonomeItem_.id, ids) }
                .find()
                .sortedWith(ComparatorCompat.comparing<PhonomeItem, Int> { ids.indexOf(it.id) })

            emitter.onNext(query)
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
        orderBy: Pair<@OrderType Int, Property<PresetItem>> = OrderType.OrderFlags.ASCENDING to PresetItem_.regDate,
        voiceEngine: VoiceEngine? = null
    ): Observable<List<PresetItem>> {
        return Observable.create {
            val equalPair = if (voiceEngine != null) voiceEngine.id to PresetItem_.engine else null
            val list =
                nativeQuerySearch(presetBox, page, limit, searchTitle to PresetItem_.title, orderBy, equal = equalPair)

            it.onNext(list)
        }
    }

    /**
     * get [StoryItem] with given [id]
     *
     * @param id [StoryItem_.id] to find
     * @return searched [StoryItem]
     */
    fun getStoryItem(id: Long): Observable<StoryItem> {
        return Observable.create {
            val query = storyBox.query {
                equal(StoryItem_.id, id)
            }.findFirst()

            if (query != null) {
                it.onNext(query)
            } else {
                it.onError(IOException("Not found StoryItem with id $id"))
            }
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
        localPlayable: Boolean = false,
        orderFavorite: Boolean = false
    ): Observable<List<StoryItem>> {
        return updateStoryItemPath()
            .flatMap {
                val notEqual = if (localPlayable) "" to StoryItem_.localPath else null

                val list = nativeQuerySearch(storyBox, page, limit, searchTitle to StoryItem_.title, orderBy, notEqual)
                    .map { item -> item.findMetadata() }

                if (orderFavorite) {
                    val nonFavorite = list.filter { item -> item.favoriteFlag.not() }
                        .sortedBy { item -> item.regDate }
                    val favorite = list.minus(nonFavorite).sortedBy { item -> item.regDate }

                    val result = mutableListOf<StoryItem>().apply {
                        addAll(favorite)
                        addAll(nonFavorite)
                    }

                    Observable.just(result)
                } else {
                    Observable.just(list)
                }
            }
    }

    /**
     * get [StoryItem] with searched all associated ids in [VoiceItem], [PhonomeItem]
     *
     * @param storyItemId search data with [StoryItem.id]
     * @return [StoryItem] with assign all associated transit data.
     */
    fun getSynthesisData(storyItemId: Long): Observable<StoryItem> {
        return Observable.create { emitter ->
            // get Latest version of 'StoryItem'
            val storyItem = storyBox.query {
                this.equal(StoryItem_.id, storyItemId)
            }.findFirst()

            if (storyItem == null) {
                emitter.onError(NullPointerException())
                return@create
            }

            storyItem.apply { this.findMetadata() }

            // Find all associated 'VoiceItem' with voiceIds.
            val voiceItemIds = storyItem.voicesIds.toLongArray()
            val voiceItems = voiceBox.query {
                this.`in`(VoiceItem_.id, voiceItemIds)
            }.find()

            // Find all associated 'PhonomeItem' with phonomeIds and assign that.
            voiceItems.map {
                it.apply { it.findMetaData() }
            }

            // apply final result
            storyItem.apply {
                this.voiceEntries = voiceItems
            }

            emitter.onNext(storyItem)
        }
    }

    /**
     * get [VoiceItem] with given [id]
     *
     * @param id [VoiceItem_.id] to find
     * @return searched [VoiceItem]
     */
    fun getVoiceItem(id: Long): Observable<VoiceItem> {
        return Observable.create {
            val query = voiceBox.query {
                equal(VoiceItem_.id, id)
            }.findFirst()

            if (query != null) {
                it.onNext(query)
            } else {
                it.onError(IOException("Not found VoiceItem with id $id"))
            }
        }
    }

    /**
     * get List of [VoiceItem] by given options without [VoiceEngine.Break] and duplicate [VoiceItem.contentOrigin]
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
            val list = nativeQuerySearch(voiceBox, -1, -1, searchTitle to VoiceItem_.contentOrigin, orderBy)
                .map { it.findMetaData() }
                .filter { it.engine != VoiceEngine.Break }
                .distinctBy { it.contentOrigin }

            emitter.onNext(list)
        }
    }

    /**
     * get List of [VoiceItem] which associated with given [StoryItem]
     *
     * it consider manually sorted when user changes order in
     * [com.github.windsekirun.yukarisynthesizer.swipe.SwipeOrderActivity]
     *
     * @param storyItem associated data
     * @return searched list by given [storyItem]
     */
    fun getVoiceListAssociatedStoryItem(storyItem: StoryItem): Observable<List<VoiceItem>> {
        return Observable.create { emitter ->
            val ids = storyItem.voicesIds.toLongArray()

            // kotlin-ported version of https://stackoverflow.com/a/45699250
            val query = voiceBox.query { this.`in`(VoiceItem_.id, ids) }
                .find()
                .map { it.findMetaData() }
                .sortedWith(ComparatorCompat.comparing<VoiceItem, Int> { ids.indexOf(it.id) })

            emitter.onNext(query)
        }
    }

    /**
     * remove StoryItem with autoRemove [VoiceItem] and [PhonomeItem] which not used.
     *
     * @param storyItem target item
     * @param autoRemove optional, remove [VoiceItem], [PhonomeItem] which not used any stories.
     * @return result of operation.
     */
    fun removeStoryItem(storyItem: StoryItem, autoRemove: Boolean = true): Observable<Boolean> {
        return Observable.create { emitter ->
            // remove given [StoryItem]
            storyBox.remove(storyItem)

            if (autoRemove) {
                // remove unused voices which doesn't used in any stories.
                val usedVoiceIds = storyBox.all.flatMap { it.voicesIds }.distinctBy { it }.toLongArray()
                val voiceQuery = voiceBox.query {
                    this.notIn(VoiceItem_.id, usedVoiceIds)
                }.find()

                voiceBox.remove(voiceQuery)

                // remove unused phonomes with doesn't used in any voices
                removeUnusedPhonomes()
            }

            emitter.onNext(true)
        }
    }

    /**
     * remove [VoiceItem] with given [VoiceItem.id]
     *
     * @param voiceItemId remove to id
     * @param autoRemove optional, remove [VoiceItem], [PhonomeItem] which not used any voices and remove associated
     * [StoryItem.voicesIds] which hold given [VoiceItem.id]
     */
    fun removeVoiceItem(voiceItemId: Long, autoRemove: Boolean = false): Observable<Boolean> {
        return Observable.create { emitter ->
            voiceBox.remove(voiceItemId)

            if (autoRemove) {
                // remove unused phonomes
                removeUnusedPhonomes()

                // remove associated [StoryItem.voiceIds] which hold given [VoiceItem.id]
                val storyItemUsed = storyBox.all.filter { it.voicesIds.contains(voiceItemId) }
                    .map { it.apply { it.voicesIds = it.voicesIds.minusElement(voiceItemId) } }
                    .toList()

                storyBox.put(storyItemUsed)
            }

            emitter.onNext(true)
        }
    }

    /**
     * request Synthesis with given [storyItem]
     *
     * @param storyItem [StoryItem] to synthesis
     * @return updated [StoryItem]
     */
    fun requestSynthesis(storyItem: StoryItem, path: String = ""): Single<StoryItem> {
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
                    sink.writeAll(it.body()?.source() as BufferedSource)
                    return@use pcmFile
                })
            }.flatMap {
                Single.create<StoryItem> { emitter ->
                    ffmpeg.execute(ffmpegCommend, object : ExecuteBinaryResponseHandler() {
                        override fun onSuccess(message: String?) {
                            super.onSuccess(message)
                            emitter.onSuccess(storyItem)

                        }

                        override fun onFailure(message: String?) {
                            super.onFailure(message)
                            emitter.onError(IOException("Cannot convert by FFmpeg"))
                        }
                    })
                }
            }
    }

    private fun StoryItem.addStoryLocalPath(): StoryItem {
        if (localPath.isNotEmpty()) return this

        val file = File(application.getExternalFilesDir(null), "/local/$title.mp3")
        file.parentFile.mkdirs()

        this.localPath = file.absolutePath
        storyBox.put(this)

        return this
    }

    private fun StoryItem.findMetadata() = this.apply {
        this.regDateFormat = this.regDate.asDateString("yyyy. MM. dd.")
    }

    private fun VoiceItem.findMetaData() = this.apply {
        val ids = this.phonomeIds.toLongArray()
        val query = phonomeBox.query {
            this.`in`(PhonomeItem_.id, ids)
        }.find()

        this.phonomes = query
    }

    private fun getInternalPresetItem(voiceEngine: VoiceEngine): PresetItem {
        return PresetItem().apply {
            this.engine = voiceEngine
            this.pitch = 1.0
            this.range = 1.0
            this.rate = 1.0
            this.volume = 1.0
            this.default = true
            this.title = "Default"
        }
    }

    private fun <ENTITY> nativeQuerySearch(
        box: Box<ENTITY>,
        page: Int,
        limit: Long,
        searchTitle: Pair<String, Property<ENTITY>>,
        orderBy: Pair<@OrderType Int, Property<ENTITY>>,
        notEqual: Pair<String, Property<ENTITY>>? = null,
        equal: Pair<String, Property<ENTITY>>? = null
    ): MutableList<ENTITY> {
        val query = box.query {
            if (searchTitle.first.isNotEmpty()) this.contains(searchTitle.second, searchTitle.first)
            this.order(orderBy.second, orderBy.first)
            if (notEqual != null) this.notEqual(notEqual.second, notEqual.first)
            if (equal != null) this.equal(equal.second, equal.first)
        }

        return if (page != -1 && limit != -1L) {
            val offset = (page - 1) * limit
            query.find(offset, limit)
        } else {
            query.find()
        }
    }

    private fun removeUnusedPhonomes() {
        val usedPhonomeIds = voiceBox.all.flatMap { it.phonomeIds }.distinctBy { it }.toLongArray()
        val phonomeQuery = phonomeBox.query {
            this.notIn(PhonomeItem_.id, usedPhonomeIds)
        }.find()

        phonomeBox.remove(phonomeQuery)
    }

    private fun updateStoryItemPath(): Observable<Int> {
        return Observable.create { emitter ->
            val notEqual = "" to StoryItem_.localPath
            val orderBy = OrderType.OrderFlags.ASCENDING to StoryItem_.regDate

            // find local playable list
            val list = nativeQuerySearch(
                storyBox, -1, -1, "" to StoryItem_.title, orderBy, notEqual
            )

            val target = list.asSequence()
                .filter { it.localPath.isNotEmpty() }
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