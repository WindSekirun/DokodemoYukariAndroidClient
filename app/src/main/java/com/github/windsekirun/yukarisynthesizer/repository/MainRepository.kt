package com.github.windsekirun.yukarisynthesizer.repository

import com.github.windsekirun.yukarisynthesizer.net.JSONService

import javax.inject.Singleton
import javax.inject.Inject

/**
 * BaseApp-BasicSet
 * Class: MainRepository
 * Created by winds on 2018-10-29.
 *
 *
 * Description:
 */
@Singleton
class MainRepository @Inject
constructor(private val mService: JSONService)
