package com.github.windsekirun.yukarisynthesizer.main

import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.windsekirun.baseapp.base.BaseActivity
import com.github.windsekirun.daggerautoinject.InjectActivity
import com.github.windsekirun.yukarisynthesizer.R
import com.github.windsekirun.yukarisynthesizer.databinding.MainActivityBinding
import com.github.windsekirun.yukarisynthesizer.main.drawer.MainDrawerFragment
import com.github.windsekirun.yukarisynthesizer.main.preset.MainPresetFragment
import com.github.windsekirun.yukarisynthesizer.main.story.MainStoryFragment
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

/**
 * DokodemoYukariAndroidClient
 * Class: ${NAME}
 * Created by Pyxis on 2018-11-20.
 *
 *
 * Description:
 */

@InjectActivity
class MainActivity : BaseActivity<MainActivityBinding>(), HasSupportFragmentInjector {
    private lateinit var viewModel: MainViewModel
    @Inject lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        viewModel = getViewModel(MainViewModel::class.java)
        mBinding.viewModel = viewModel

        setSupportActionBar(mBinding.bottomAppBar)

        // make darker if available.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = ContextCompat.getColor(this, R.color.status_color)
        }

        replaceFragment(MainStoryFragment::class, 0)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.menu_home_setting -> viewModel.moveSettingActivity()
            android.R.id.home -> showBottomDrawer()
        }

        return true
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = fragmentInjector

    private fun showBottomDrawer() {
        val drawerFragment = MainDrawerFragment()
        drawerFragment.onMenuClickListener = {
            when (it) {
                R.id.menu_drawer_story -> replaceFragment(MainStoryFragment::class, 0)
                R.id.menu_drawer_preset -> replaceFragment(MainPresetFragment::class, 1)
                R.id.menu_drawer_play -> replaceFragment(MainStoryFragment::class, 2)
                R.id.menu_drawer_import -> replaceFragment(MainStoryFragment::class, 3)
            }
        }
        drawerFragment.pagePosition = viewModel.pagePosition
        drawerFragment.show(supportFragmentManager, drawerFragment.tag)
    }

    private fun <T : Fragment> replaceFragment(cls: KClass<T>, pagePosition: Int = 0) {
        if (viewModel.pagePosition == pagePosition) return // if pagePosition is same, block that.
        viewModel.pagePosition = pagePosition

        val fragment = cls.createInstance()
        supportFragmentManager.beginTransaction().replace(R.id.container, fragment).commitAllowingStateLoss()
    }
}