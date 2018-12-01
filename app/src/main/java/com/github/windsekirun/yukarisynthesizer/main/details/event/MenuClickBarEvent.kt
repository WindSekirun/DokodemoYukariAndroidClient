package com.github.windsekirun.yukarisynthesizer.main.details.event

/**
 * DokodemoYukariAndroidClient
 * Class: MenuClickBarEvent
 * Created by Pyxis on 12/1/18.
 *
 *
 * Description:
 */
class MenuClickBarEvent(val mode: Mode) {

    enum class Mode {
        Play, Star, Share
    }
}
