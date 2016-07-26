package com.dda.mobilesafe.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by nuo on 2016/4/6.
 */
public class ToastUtils {

    public static void showToast(Context ctx, String text) {
        Toast.makeText(ctx, text, Toast.LENGTH_SHORT).show();
    }
}
