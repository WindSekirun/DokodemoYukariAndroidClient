package com.github.windsekirun.yukarisynthesizer.notification

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.windsekirun.yukarisynthesizer.main.MainActivity


class BackgroundActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onNewIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        startApp(intent)
        finish()
    }

    fun startApp(intent: Intent) {
        if (isTaskRoot) {
            goIntro(this, intent)
        } else {
            goMain(this, intent)
        }
    }

    private fun goIntro(context: Context, intent: Intent) {
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.setClass(context, MainActivity::class.java)
        context.startActivity(intent)
    }

    private fun goMain(context: Context, intent: Intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        intent.setClass(context, MainActivity::class.java)
        context.startActivity(intent)
    }
}
