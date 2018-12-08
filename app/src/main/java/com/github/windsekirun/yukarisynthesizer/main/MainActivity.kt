package com.github.windsekirun.yukarisynthesizer.main

import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.windsekirun.baseapp.base.BaseActivity
import com.github.windsekirun.baseapp.module.back.DoubleBackInvoker
import com.github.windsekirun.daggerautoinject.InjectActivity
import com.github.windsekirun.yukarisynthesizer.R
import com.github.windsekirun.yukarisynthesizer.databinding.MainActivityBinding
import com.github.windsekirun.yukarisynthesizer.dialog.PresetDialog
import com.github.windsekirun.yukarisynthesizer.main.details.MainDetailsFragment
import com.github.windsekirun.yukarisynthesizer.main.details.dialog.MainDetailsVoiceFragment
import com.github.windsekirun.yukarisynthesizer.main.details.event.AddVoiceEvent
import com.github.windsekirun.yukarisynthesizer.main.details.event.MenuClickBarEvent
import com.github.windsekirun.yukarisynthesizer.main.drawer.MainDrawerFragment
import com.github.windsekirun.yukarisynthesizer.main.impl.OnBackPressedListener
import com.github.windsekirun.yukarisynthesizer.main.preset.MainPresetFragment
import com.github.windsekirun.yukarisynthesizer.main.story.MainStoryFragment
import com.github.windsekirun.yukarisynthesizer.main.story.event.RefreshBarEvent
import com.github.windsekirun.yukarisynthesizer.utils.reveal.CircularRevealUtils
import com.github.windsekirun.yukarisynthesizer.utils.reveal.RevealSettingHolder
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
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
    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

    private val addVisibilityChanged: FloatingActionButton.OnVisibilityChangedListener =
        object : FloatingActionButton.OnVisibilityChangedListener() {
            override fun onShown(fab: FloatingActionButton?) {
                super.onShown(fab)
                RevealSettingHolder.revealSetting =
                        CircularRevealUtils.RevealSetting.with(mBinding.fab, mBinding.container)
            }

            override fun onHidden(fab: FloatingActionButton?) {
                super.onHidden(fab)
                mBinding.bottomAppBar.fabAlignmentMode = viewModel.currentFabAlignmentMode
                mBinding.bottomAppBar.replaceMenu(
                    if (isInDetails()) {
                        R.menu.menu_details
                    } else {
                        R.menu.menu_main
                    }
                )
                fab?.show(this)
            }
        }

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

        mBinding.fab.setOnClickListener {
            if (isInDetails()) {
                addNewVoices()
                return@setOnClickListener
            }

            when (viewModel.pagePosition) {
                0 -> addNewStory()
                1 -> addNewPreset()
            }
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
            R.id.menu_details_play -> EventBus.getDefault().post(MenuClickBarEvent(MenuClickBarEvent.Mode.Play))
            R.id.menu_details_star -> EventBus.getDefault().post(MenuClickBarEvent(MenuClickBarEvent.Mode.Star))
            R.id.menu_details_remove -> EventBus.getDefault().post(MenuClickBarEvent(MenuClickBarEvent.Mode.Remove))
            android.R.id.home -> showBottomDrawer()
        }

        return true
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = fragmentInjector

    override fun onBackPressed() {
        if (isInDetails()) {
            supportFragmentManager.fragments
                .filterIsInstance(OnBackPressedListener::class.java)
                .forEach { it.onBackPressed() }
        } else {
            DoubleBackInvoker.execute(getString(R.string.main_double_back_invoker))
        }
    }

    @Subscribe
    fun onRefreshBarEvent(event: RefreshBarEvent) {
        toggleBottomBar(event.attached)
    }

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
        if (viewModel.pagePosition == pagePosition) return
        viewModel.pagePosition = pagePosition

        val fragment = cls.createInstance()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, fragment)
            .commitAllowingStateLoss()
    }

    private fun toggleBottomBar(attached: Boolean) {
        viewModel.currentFabAlignmentMode = if (attached) {
            BottomAppBar.FAB_ALIGNMENT_MODE_END
        } else {
            BottomAppBar.FAB_ALIGNMENT_MODE_CENTER
        }

        mBinding.fab.hide(addVisibilityChanged)
        invalidateOptionsMenu()
        mBinding.bottomAppBar.navigationIcon = if (attached) {
            null
        } else {
            ContextCompat.getDrawable(this, R.drawable.ic_menu_black_24dp)
        }
    }

    private fun addNewStory() {
        RevealSettingHolder.revealSetting = CircularRevealUtils.RevealSetting.with(mBinding.fab, mBinding.container)
        toggleBottomBar(true)

        val fragment = MainDetailsFragment().apply {
            this.revealSetting = RevealSettingHolder.revealSetting
        }

        supportFragmentManager
            .beginTransaction()
            .setReorderingAllowed(true)
            .add(R.id.container, fragment)
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }

    private fun addNewVoices() {
        val voiceFragment = MainDetailsVoiceFragment()
        voiceFragment.onMenuClickListener = {
            EventBus.getDefault().post(AddVoiceEvent(it))
        }
        voiceFragment.show(supportFragmentManager, voiceFragment.tag)
    }

    private fun isInDetails() = viewModel.currentFabAlignmentMode == BottomAppBar.FAB_ALIGNMENT_MODE_END

    private fun addNewPreset() {
        val presetDialog = PresetDialog(this)
        presetDialog.show()
    }
}