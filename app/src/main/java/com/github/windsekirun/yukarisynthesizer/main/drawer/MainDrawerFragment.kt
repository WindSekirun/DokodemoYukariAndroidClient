package com.github.windsekirun.yukarisynthesizer.main.drawer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import com.github.windsekirun.yukarisynthesizer.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.main_drawer_fragment.*


/**
 * DokodemoYukariAndroidClient
 * Class: MainDrawerFragment
 * Created by Pyxis on 2018-11-26.
 *
 *
 * Description:
 */
class MainDrawerFragment : BottomSheetDialogFragment() {
    var onMenuClickListener: ((Int) -> Unit)? = null
    var pagePosition: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.main_drawer_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        navigationView.setNavigationItemSelectedListener {
            uncheckAllMenuItems(navigationView.menu)

            onMenuClickListener?.invoke(it.itemId)
            dismiss()
            true
        }

        navigationView.post {
            val item = navigationView.menu.getItem(pagePosition)
            if (item != null) {
                navigationView.setCheckedItem(item.itemId)
            }
        }
    }

    private fun uncheckAllMenuItems(menu: Menu) {
        val size = menu.size()
        for (i in 0 until size) {
            val item = menu.getItem(i)
            if (item.hasSubMenu()) {
                uncheckAllMenuItems(item.subMenu)
            } else {
                item.isChecked = false
            }
        }
    }
}
