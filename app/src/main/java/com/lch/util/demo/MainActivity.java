package com.lch.util.demo;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.lch.util.IOUtils;
import com.lch.util.executor.ResultDto;
import com.lch.util.http.HttpUtils;

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

    }

    private byte[] bmpToArray(Bitmap bmp) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
       byte[] byteArray = stream.toByteArray();
        IOUtils.closeQuietly(stream);

        return byteArray;
    }

}
