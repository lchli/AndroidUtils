package com.lch.util.demo;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.lch.util.executor.ResultDto;
import com.lch.util.http.HttpUtils;

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
                Map<String,Object> params = new HashMap<>();

                params.put("fileType", "video");
                params.put("file", new File("/storage/emulated/999/Pictures/WeiXin/wx_camera_1597632437686.mp4"));
                params.put("userToken", "userToken");

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
}
