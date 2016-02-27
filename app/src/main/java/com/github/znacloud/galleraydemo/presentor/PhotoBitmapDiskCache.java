package com.github.znacloud.galleraydemo.presentor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Parcel;
import android.support.v4.graphics.BitmapCompat;
import android.text.TextUtils;

import com.github.znacloud.galleraydemo.util.BitmapUtils;
import com.github.znacloud.galleraydemo.util.DiskLruCache;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;

/**
 * Created by Stephan on 2016/1/31.
 */
public class PhotoBitmapDiskCache{
    private static final int APP_VERSION = 1;
    private static final int VALUE_COUNT = 1;
    private static final String DEFAULT_CACHE_DIR = "PhotoBitmapDiskCache";
    private static final long DEFAULT_CACHE_SIZE = 100*1024*1024; // 100MB
    private static DiskLruCache mDiskCache;
    private static PhotoBitmapDiskCache mInstance;
    private static String mCacheDir;

    /**
     * 构造函数，为保证全局唯一性，私有化
     * @param cacheDir 缓存目录
     * @param cacheSize 缓存空间的可用大小
     */
    private PhotoBitmapDiskCache(File cacheDir,long cacheSize){
        try {
            mDiskCache = DiskLruCache.open(cacheDir,APP_VERSION,VALUE_COUNT,cacheSize);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取单例，可能耗时，所以建议在工作线程中调用
     * @param cacheDir 缓存文件
     * @param cacheSize 缓存空间大小,已字节为单位
     * @return PhotoBitmapDiskCache
     */
    public static synchronized PhotoBitmapDiskCache getDiskCache(File cacheDir,long cacheSize){
        if(mDiskCache == null || mInstance == null || !TextUtils.equals(cacheDir.getName(),mCacheDir)) {
                mInstance = new PhotoBitmapDiskCache(cacheDir, cacheSize);
                mCacheDir = cacheDir.getName();
        }
        return mInstance;

    }

//
//    private static synchronized PhotoBitmapDiskCache getDiskCache(File paretnDir){
//        if(mDiskCache == null || mInstance == null || !TextUtils.equals(DEFAULT_CACHE_DIR,mCacheDir)) {
//                File cacheDirFile = new File(paretnDir,DEFAULT_CACHE_DIR);
//                mInstance = new PhotoBitmapDiskCache(cacheDirFile, DEFAULT_CACHE_SIZE);
//                mCacheDir = DEFAULT_CACHE_DIR;
//        }
//        return mInstance;
//    }

    public void put(String key,Bitmap value){
        if(mDiskCache == null) return;
        OutputStream outputStream = null;
        DiskLruCache.Editor editor = null;
        try {
             editor= mDiskCache.edit(key.hashCode()+"");
             outputStream = editor.newOutputStream(0);
            value.compress(Bitmap.CompressFormat.WEBP,100,outputStream);
            editor.commit();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                editor.abort();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }finally {
            if(outputStream != null){
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Bitmap get(String key){
        if(mDiskCache == null) return null;
        InputStream inputStream = null;
        Bitmap bitmap = null;
        DiskLruCache.Editor editor = null;
        try {
             editor = mDiskCache.edit(key.hashCode()+"");

            inputStream = editor.newInputStream(0);
            bitmap = BitmapFactory.decodeStream(inputStream);
            editor.abort();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(inputStream != null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bitmap;
    }


}
