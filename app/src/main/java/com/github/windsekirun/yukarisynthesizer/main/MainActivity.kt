package com.github.windsekirun.yukarisynthesizer.main

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.MenuRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.windsekirun.baseapp.base.BaseActivity
import com.github.windsekirun.baseapp.module.back.DoubleBackInvoker
import com.github.windsekirun.daggerautoinject.InjectActivity
import com.github.windsekirun.yukarisynthesizer.R
import com.github.windsekirun.yukarisynthesizer.databinding.MainActivityBinding
import com.github.windsekirun.yukarisynthesizer.main.details.MainDetailsFragment
import com.github.windsekirun.yukarisynthesizer.main.event.AddFragmentEvent
import com.github.windsekirun.yukarisynthesizer.main.event.CloseSpeedDialEvent
import com.github.windsekirun.yukarisynthesizer.main.event.InvokeBackEvent
import com.github.windsekirun.yukarisynthesizer.main.event.SwapDetailEvent
import com.github.windsekirun.yukarisynthesizer.main.impl.OnBackPressedListener
import com.github.windsekirun.yukarisynthesizer.main.story.MainStoryFragment
import com.github.windsekirun.yukarisynthesizer.utils.reveal.CircularRevealUtils
import com.github.windsekirun.yukarisynthesizer.utils.reveal.RevealSettingHolder
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import org.greenrobot.eventbus.Subscribe
import javax.inject.Inject

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        viewModel = getViewModel(MainViewModel::class.java)
        mBinding.viewModel = viewModel

        // make darker if available.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = ContextCompat.getColor(this, R.color.status_color)
        }

        init()
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = fragmentInjector

    override fun onBackPressed() {
        if (mBinding.speedDial.isOpen) {
            closeSpeedDial()
            return
        }

        if (viewModel.shownDetail) {
            supportFragmentManager.fragments
                .filterIsInstance(OnBackPressedListener::class.java)
                .forEach { it.onBackPressed() }
        } else {
            DoubleBackInvoker.execute(getString(R.string.main_double_back_invoker))
        }
    }

    @Subscribe
    fun onAddFragmentEvent(event: AddFragmentEvent<*>) {
        replaceFragment(event.fragment, event.animated, event.backStack, event.reveal)
    }

    @Subscribe
    fun onSwapDetailEvent(event: SwapDetailEvent) {
        swapDetail(event.exitDetail)
    }

    @Subscribe
    fun onInvokeBackEvent(event: InvokeBackEvent) {
        onBackPressed()
    }

    @Subscribe
    fun onCloseSpeedDialEvent(event: CloseSpeedDialEvent) {
        closeSpeedDial()
    }

    private fun closeSpeedDial() {
        mBinding.speedDial.close(true)
    }

    private fun init() {
        switchSpeedDialMenu(R.menu.menu_main_speed_dial)
        switchToolbarMenu(R.menu.menu_main)

        replaceFragment(MainStoryFragment(), true, false, false)
    }

    private fun switchToolbarMenu(@MenuRes menuRes: Int) {
        mBinding.toolbar.menu.clear() // clear before inflate new menu
        mBinding.toolbar.inflateMenu(menuRes)
    }

    private fun switchSpeedDialMenu(@MenuRes menuRes: Int) {
        mBinding.speedDial.inflate(menuRes)
    }

    private fun switchToolbarTitle(@StringRes title: Int) {
        mBinding.toolbar.title = getString(title)
    }

    private fun <T : Fragment> replaceFragment(fragment: T, animated: Boolean, backStack: Boolean, reveal: Boolean) {
        val revealSetting = if (reveal) {
            CircularRevealUtils.RevealSetting.with(mBinding.speedDial, mBinding.container)
        } else null

        RevealSettingHolder.revealSetting = revealSetting

        val transaction = supportFragmentManager.beginTransaction()
        if (animated) transaction.setCustomAnimations(R.anim.fadein_fragment, R.anim.fadeout_fragment)

        if (backStack) {
            transaction.setReorderingAllowed(true)
                .add(R.id.container, fragment)
                .addToBackStack(null)
                .commitAllowingStateLoss()
        } else {
            transaction.replace(R.id.container, fragment)
                .commitAllowingStateLoss()
        }
    }

    private fun swapDetail(exitDetail: Boolean) {
        val toolbarRes = if (exitDetail) R.menu.menu_main else R.menu.menu_details_top
        val speedDialRes = if (exitDetail) R.menu.menu_main_speed_dial else R.menu.menu_details_speed_dial
        val titleRes = R.string.story_title

        mBinding.toolbar.navigationIcon = if (exitDetail) null else getDrawable(R.drawable.ic_arrow_left_black_24dp)
        switchToolbarMenu(toolbarRes)
        switchSpeedDialMenu(speedDialRes)
        switchToolbarTitle(titleRes)

        viewModel.shownDetail = !exitDetail
    }
}