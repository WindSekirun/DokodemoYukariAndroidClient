package com.github.windsekirun.yukarisynthesizer.repository;

import com.github.windsekirun.yukarisynthesizer.net.JSONService;

import javax.inject.Singleton;
import javax.inject.Inject;

/**
 * BaseApp-BasicSet
 * Class: MainRepository
 * Created by winds on 2018-10-29.
 * <p>
 * Description:
 */
@Singleton
public class MainRepository {
    private JSONService mService;

    @Inject
    public MainRepository(JSONService service) {
        mService = service;
    }
}
