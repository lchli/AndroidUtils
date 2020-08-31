package com.lch.util.img;

import android.graphics.Bitmap;

public class CircleTransform implements BitmapTransform {


    private int borderColor;
    private float borderWidth;

    public CircleTransform(float borderWidth, int borderColor) {
        this.borderWidth = borderWidth;
        this.borderColor = borderColor;
    }


    @Override
    public Bitmap transform(Bitmap toTransform) {
        if (toTransform == null) {
            return null;
        }

        return ImageUtil.toRound(toTransform, (int) borderWidth, borderColor, true);
    }


}
