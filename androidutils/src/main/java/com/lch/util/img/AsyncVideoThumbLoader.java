package com.lch.util.img;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;

import com.lch.util.ContextUtil;

public class AsyncVideoThumbLoader extends AsyncImgLoader {

    @Override
    protected Bitmap loadImpl(ImgLoadParam param) {
        return getVideoThumbnail(param.model);
    }

    /**
     * 获取本地视频的第一帧
     */
    public static Bitmap getVideoThumbnail(Object model) {
        Bitmap bitmap = null;
        //MediaMetadataRetriever 是android中定义好的一个类，提供了统一
        //的接口，用于从输入的媒体文件中取得帧和元数据；
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            //根据文件路径获取缩略图
            setdataSource(retriever, model);
            //获得第一帧图片
            bitmap = retriever.getFrameAtTime();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            retriever.release();
        }
        return bitmap;
    }

    private static void setdataSource(MediaMetadataRetriever retriever, Object model) {
        if (model instanceof String) {
            retriever.setDataSource((String) model);
            return;
        }
        if (model instanceof Uri) {
            retriever.setDataSource(ContextUtil.getContext(), (Uri) model);
            return;
        }

    }
}
