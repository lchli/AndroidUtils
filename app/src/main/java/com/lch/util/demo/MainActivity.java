package com.lch.util.demo;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

import com.lch.util.IOUtils;
import com.lch.util.executor.ResultDto;
import com.lch.util.http.HttpUtils;
import com.lch.util.img.AsyncDataLoader;
import com.lch.util.img.AsyncLoaders;
import com.lch.util.img.CircleTransform;
import com.lch.util.img.ImgLoadParam;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity {
    public static final String DOMAIN = "http://api.fanyan88.com";
    public static final String UPLOAD_VIDEO = DOMAIN + "/upload/imagesAndVideo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Thread(new Runnable() {
            @Override
            public void run() {
                File f= new File(Environment.getExternalStorageDirectory(),"shot.jpg");
                Bitmap t= BitmapFactory.decodeFile(f.getAbsolutePath());

                Map<String,Object> params = new HashMap<>();

                params.put("fileType", "video");
                params.put("file", bmpToArray(t));
                params.put("userToken", "1559399493e436e47362adfb47c3b0d7");

                ResultDto<String> response = HttpUtils.postMultiPart(
                        UPLOAD_VIDEO,
                        null,
                        params, 30_000

                );

                Log.e("s","upload2 response.data:"+response.getMsg());
                Log.e("s","upload2 response.data:"+response.getData());
            }
        }).start();

        final ImageView iv=new ImageView(this);

        AsyncLoaders.IMG.load(ImgLoadParam.create().setModel(""), new AsyncDataLoader.AsyncDataTargetView<Bitmap>() {
            @Override
            public void bindResult(Bitmap res) {
                iv.setImageBitmap(res);
            }

            @Override
            public void setViewUid(Object uid) {
                iv.setTag(uid);

            }

            @Override
            public Object getViewUid() {
                return iv.getTag();
            }
        }, new CircleTransform(5f, Color.BLACK));

    }

    private byte[] bmpToArray(Bitmap bmp) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
       byte[] byteArray = stream.toByteArray();
        IOUtils.closeQuietly(stream);

        return byteArray;
    }

}
