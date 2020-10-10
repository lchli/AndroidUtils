package com.lch.util.img;

public class ImgLoadParam {
    public Object model;
    public int resizeW;
    public int resizeH;

    public static ImgLoadParam create(){
        return new ImgLoadParam();
    }

    public ImgLoadParam setModel(Object model) {
        this.model = model;
        return this;
    }

    public ImgLoadParam setResizeW(int resizeW) {
        this.resizeW = resizeW;
        return this;
    }

    public ImgLoadParam setResizeH(int resizeH) {
        this.resizeH = resizeH;
        return this;
    }
}
