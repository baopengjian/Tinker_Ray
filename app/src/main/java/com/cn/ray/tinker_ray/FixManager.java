package com.cn.ray.tinker_ray;


import android.content.Context;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.HashSet;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

public class FixManager {
    private static final FixManager ourInstance = new FixManager();

    public static FixManager getInstance() {
        return ourInstance;
    }

    private HashSet<File> loaded = new HashSet<>();
    private FixManager() {
    }

    public void loadDex(Context context) {
        if (context == null) {
            return;
        }
        File filesDir = context.getDir("odex", Context.MODE_PRIVATE);
        File[]  listFiles=filesDir.listFiles();
        for (File file : listFiles) {
//            如果是classes开到   。dex
            if(file.getName().endsWith(".dex")){
                loaded.add(file);
            }
        }

//        修复
        String optimizeDir = filesDir.getAbsolutePath() + File.separator + "opt_dex";
        File fopt = new File(optimizeDir);
        if (!fopt.exists()) {
            fopt.mkdirs();
        }
        for (File dex : loaded) {
//              private final Element[] dexElements;

//            //        对象成员变量      对象 还原     ----数组   反射调用
//    ------------------------------我们的--------------------------------------------------------
            DexClassLoader dexClassloader = new DexClassLoader(dex.getAbsolutePath(), optimizeDir,
                    null, context.getClassLoader());
//         BaseClassLoader  ----dexPathList   ---->dexElements;
            try {
                Class myDexClazzLoader=Class.forName("dalvik.system.BaseDexClassLoader");
                Field myPathListFiled=myDexClazzLoader.getDeclaredField("pathList");
                myPathListFiled.setAccessible(true);
//dexPathList
                Object myPathListObject =myPathListFiled.get(dexClassloader);

                Class  myPathClazz=myPathListObject.getClass();
                Field  myElementsField = myPathClazz.getDeclaredField("dexElements");//dexElements

                myElementsField.setAccessible(true);
                Object myElements = myElementsField.get(myPathListObject);// Element[]   dexElements
//                myElements  -----》  private final Element[] dexElements;
//                天之道里面去  1号房间

//----------------------------------系统的--------------------------------------------------------
//              PathClassLoader   PathList Element

                PathClassLoader pathClassLoader = (PathClassLoader) context.getClassLoader();
                Class baseDexClazzLoader=Class.forName("dalvik.system.BaseDexClassLoader");
                Field  pathListFiled=baseDexClazzLoader.getDeclaredField("pathList");
                pathListFiled.setAccessible(true);
                Object pathListObject = pathListFiled.get(pathClassLoader);

                Class  systemPathClazz=pathListObject.getClass();
                Field  systemElementsField = systemPathClazz.getDeclaredField("dexElements");
                systemElementsField.setAccessible(true);
                Object systemElements=systemElementsField.get(pathListObject);
//-----------------systemElements-- 所有的房间   5   ----------1+5  -----------------------------------------------
// 其他地方  新建留个房间
//                dalvik.system.Element[] dexElements;
                Class elemnt = systemElements.getClass().getComponentType();
                int systemLength=Array.getLength(systemElements);
                int myLength = Array.getLength(myElements);
                //                生成一个新的 数组   类型为Element类型
                Object newElementArray= Array.newInstance(elemnt, systemLength + myLength);

                for(int i=0;i<myLength+systemLength;i++) {
                    if (i < myLength) {
//                      先插入修复包的dex yes  1
                        Array.set(newElementArray, i, Array.get(myElements,i));
                    }else {
//                      系统的            no  2 索引错了  systemElements  5
                        Array.set(newElementArray, i, Array.get(systemElements,i-myLength));
                    }
                }



                Field  elementsField=pathListObject.getClass().getDeclaredField("dexElements");;
                elementsField.setAccessible(true);

                elementsField.set(pathListObject, newElementArray);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
