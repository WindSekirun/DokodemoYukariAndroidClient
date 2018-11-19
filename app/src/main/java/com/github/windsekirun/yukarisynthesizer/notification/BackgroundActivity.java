package com.github.windsekirun.yukarisynthesizer.notification;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


public class BackgroundActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onNewIntent(getIntent());
    }

    @Override
    protected void onNewIntent(final Intent intent) {
        startApp(intent);
        finish();
    }

    public void startApp(Intent intent) {
        if (isTaskRoot()) {
            goIntro(this, intent);
        } else {
            goMain(this, intent);
        }
    }

    private static void goIntro(Context context, Intent intent) {
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.setClass(context, MainActivity.class);
        context.startActivity(intent);
    }

    private static void goMain(Context context, Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        intent.setClass(context, MainActivity.class);
        context.startActivity(intent);
    }
}
