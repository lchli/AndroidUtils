package com.lch.util;

import android.app.Dialog;

public class DialogUtil {

    public static void showDialog(Dialog d) {
        try {
            d.show();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void dismissDialog(Dialog d) {
        try {
            d.dismiss();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
