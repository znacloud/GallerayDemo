package com.github.znacloud.galleraydemo.presentor;

import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

/**
 * Created by Stephan on 2016/1/24.
 */
public class PhotoBitmapCache extends LruCache<String,Bitmap>{
    private static final String TAG = PhotoBitmapCache.class.getSimpleName();
    private static PhotoBitmapCache mInstance;

    /**
     * 单例模式，私有化构造函数
     * @param cacheSize 总共的缓存空间
     */
    private PhotoBitmapCache(int cacheSize){
        super((cacheSize));
    }

    /**
     * 获取缓存单例，保证全局只有一个实例
     * @return
     */
    public static synchronized PhotoBitmapCache getCache(){
        if(mInstance ==null) {
            //获取虚拟机最大内存，即每个APP可用的内存
            int maxMemory = (int) Runtime.getRuntime().maxMemory() / 1024;
            //缓存空间设置为可用内存的1/4
            int cacheSize = maxMemory / 4;
            mInstance = new PhotoBitmapCache(cacheSize);
            Log.i(TAG,"maxMemory="+maxMemory+"KB;cacheSize="+cacheSize+"KB");
        }

        return mInstance;
    }

    @Override
    protected int sizeOf(String key, Bitmap value) {
        Log.i(TAG,key+"=>"+value.getByteCount()+"B");
        return value.getByteCount()/1024;
    }

    /**
     * 加入缓存
     * @param key 键名 通常是图片路径
     * @param value 键值 Bitmap对象
     */
    public void addBitmapToMemCache(String key,Bitmap value){
        if(getBitmapFromMemCache(key) == null){
            put(key,value);
        }
        Log.i(TAG,"free size=>"+(maxSize()-size()));
    }

    /**
     * 从缓存中获取Bitmap对象
     * @param key 键名 通常是图片路径
     * @return Bitmap对象
     */
    public Bitmap getBitmapFromMemCache(String key){
        return get(key);
    }
}
