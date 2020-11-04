package com.cn.ray.tinker_ray;

import android.app.Application;
import android.content.Context;

public class MyAplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        FixManager.getInstance().loadDex(base);
        super.attachBaseContext(base);
    }
}
