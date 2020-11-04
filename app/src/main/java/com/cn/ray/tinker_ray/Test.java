package com.cn.ray.tinker_ray;

import android.content.Context;
import android.widget.Toast;

public class Test {

    public void testFix(Context context) {
       throw new RuntimeException();
        // Toast.makeText(context, "修复成功了", Toast.LENGTH_LONG).show();
    }
}
