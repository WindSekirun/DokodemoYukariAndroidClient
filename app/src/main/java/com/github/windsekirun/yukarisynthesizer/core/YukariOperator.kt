package com.github.windsekirun.yukarisynthesizer.core

import com.github.windsekirun.yukarisynthesizer.MainApplication
import com.github.windsekirun.yukarisynthesizer.core.annotation.OrderType
import com.github.windsekirun.yukarisynthesizer.core.item.*
import com.github.windsekirun.yukarisynthesizer.repository.PreferenceRepository
import io.objectbox.Box
import io.objectbox.Property
import io.objectbox.kotlin.query
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class YukariOperator @Inject constructor(val application: MainApplication, val preferenceRepository: PreferenceRepository) {
    private val phonomeBox: Box<Phonome> by lazy { application.getBox(Phonome::class.java) }
    private val presetBox: Box<PresetItem> by lazy { application.getBox(PresetItem::class.java) }
    private val ssmlBox: Box<SSMLItem> by lazy { application.getBox(SSMLItem::class.java) }
    private val voiceBox: Box<VoiceItem> by lazy { application.getBox(VoiceItem::class.java) }

    /**
     * get List of [Phonome] by given options.
     *
     * all parameter in [getPhonomeList] are optional parameter.
     *
     * @param searchTitle return list by contains given value in [Phonome_.title]. Default value is ""
     * @param recent return list with last 10 items of [Phonome]
     * @return searched list by given options.
     */
    fun getPhonomeList(searchTitle: String = "", recent: Boolean = true): Observable<List<Phonome>> {
        return Observable.create {
            val page = if (recent) 1 else -1
            val limit = if (recent) 10L else -1L
            val orderType = if (recent) OrderType.OrderFlags.DESCENDING else OrderType.OrderFlags.ASCENDING

            val list = nativeQuerySearch(phonomeBox, searchTitle to Phonome_.origin,
                    orderType to Phonome_.regDate, page, limit)
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
    @JvmOverloads
    fun getPresetList(page: Int = -1,
                      limit: Long = 20L,
                      searchTitle: String = "",
                      orderBy: Pair<@OrderType Int, Property<PresetItem>> =
                          OrderType.OrderFlags.ASCENDING to PresetItem_.regDate)
            : Observable<List<PresetItem>> {
        return Observable.create {
            val list = nativeQuerySearch(presetBox, searchTitle to PresetItem_.title, orderBy, page, limit)
            it.onNext(list)
        }
    }

    /**
     * get List of 'Story object'(a.k.a [SSMLItem]) by given options
     *
     * all parameter in [getStoryList] are optional parameter.
     *
     * @param searchTitle return list by contains given value in [SSMLItem_.title]. Default value is ""
     * @param orderBy return list by given order. Int will indicate one value of [OrderType.OrderFlags],
     * [Property] will indicate what order can applied.
     * Default value is [OrderType.OrderFlags.ASCENDING] to [SSMLItem_.regDate]
     * @param page return list by pagination. Pagination only available when page and limit aren't indicate -1.
     * This parameter starts from 1, not 0. Default value is -1 (not used)
     * @param limit return list by pagination. Default value is 20.
     * @return searched list by given options.
     */
    @JvmOverloads
    fun getStoryList(page: Int = -1,
                     limit: Long = 20L,
                     searchTitle: String = "",
                     orderBy: Pair<@OrderType Int, Property<SSMLItem>> =
                         OrderType.OrderFlags.ASCENDING to SSMLItem_.regDate)
            : Observable<List<SSMLItem>> {
        return Observable.create {
            val list = nativeQuerySearch(ssmlBox, searchTitle to SSMLItem_.title, orderBy, page, limit)
            it.onNext(list)
        }
    }

    /**
     * Run native query search by given options.
     */
    private fun <ENTITY> nativeQuerySearch(box: Box<ENTITY>,
                                           searchTitle: Pair<String, Property<ENTITY>>,
                                           orderBy: Pair<@OrderType Int, Property<ENTITY>>,
                                           page: Int, limit: Long)
            : MutableList<ENTITY> {
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
}