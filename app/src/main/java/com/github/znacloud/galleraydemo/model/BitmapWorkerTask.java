package com.github.znacloud.galleraydemo.model;

/**
 * Created by Stephan on 2016/1/24.
 */

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.github.znacloud.galleraydemo.presentor.PhotoBitmapCache;
import com.github.znacloud.galleraydemo.presentor.PhotoBitmapDiskCache;
import com.github.znacloud.galleraydemo.util.BitmapUtils;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * 异步加载图片类
 * Object[0] 图片resId,Object[1] Resouces 对象，
 * 或者
 * Object[0] file path
 * Bitmap 得到的Bitmap对象
 */
public class BitmapWorkerTask extends AsyncTask<Object, Void, Bitmap> {
    private final WeakReference<ImageView> imageViewReference;
    private final int requireWidth;
    private final int requireHeight;
    private final PhotoBitmapCache mMemCache;
    private final File mDiskCacheDir;
    private PhotoBitmapDiskCache mDiskCache;
    private int dataResId = 0;
    private String dataFilePath = null;
    private long mDiskCacheSize = 100*1024*1024; //100M

    public BitmapWorkerTask(final ImageView imageView) {
        // 若引用，保证ImageView可以被正常回收
        imageViewReference = new WeakReference<ImageView>(imageView);
        mMemCache = PhotoBitmapCache.getCache();
        //获取磁盘缓存的目录，缓存类在后台实例化
        mDiskCacheDir = BitmapUtils.getDiskCacheDir(imageView.getContext(),"photodiskcache");

        requireWidth = imageView.getWidth();
        requireHeight = imageView.getHeight();
    }

    // 在后台加载图片
    @Override
    protected Bitmap doInBackground(Object... params) {
        Bitmap bitmap = null;
        Object param0 = params[0];
        //先从磁盘缓存获取
        bitmap = getBitmapFromDiskCache(param0.toString());
        if(bitmap != null){
            mMemCache.addBitmapToMemCache(param0.toString(), bitmap);
            return bitmap;
        }

        Log.e("DEBUG","get from others");
        //若缓存中没有，再从其他地方获取
        if(param0 instanceof Integer){
            Resources res = (Resources)params[1];
            dataResId = (int)param0;
            bitmap =  BitmapUtils.decodeSampledBitmapFromResource(res, dataResId, requireWidth, requireHeight);
        }else if(param0 instanceof String){
            dataFilePath = (String)param0;
            Log.e("FUCK","PATH=>"+dataFilePath);
            bitmap = BitmapUtils.decodeSampledBitmapFromFile(dataFilePath,requireWidth,requireHeight);
        }
        mMemCache.addBitmapToMemCache(param0.toString(), bitmap);

        //同时添加到磁盘缓存
        putBitmapToDiskCache(param0.toString(), bitmap);

        return bitmap;
    }

    private void putBitmapToDiskCache(String key, Bitmap pBitmap) {
        if(mDiskCache == null){
            mDiskCache = PhotoBitmapDiskCache.getDiskCache(mDiskCacheDir,mDiskCacheSize);
        }
        synchronized (BitmapWorkerTask.class) {
            Log.e("DEBUG","put into disk");
            mDiskCache.put(key,pBitmap);
        }
    }

    private Bitmap getBitmapFromDiskCache(String key) {
        if(mDiskCache == null){
            mDiskCache = PhotoBitmapDiskCache.getDiskCache(mDiskCacheDir,mDiskCacheSize);
        }
        synchronized (BitmapWorkerTask.class) {
            Log.e("DEBUG","get from disk");
            return mDiskCache.get(key);
        }
    }

    // 完成后返回Bitmap给UI线程
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if(isCancelled()){
            bitmap = null;
        }

        if (imageViewReference != null && bitmap != null) {
            final ImageView imageView = imageViewReference.get();
            if (getBitmapWorkerTask(imageView)== this && imageView != null) {//若imageview还没被回收则设置图片，否则啥都不做
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    public int getDataResId() {
        return dataResId;
    }

    public String getDataFilePath() {
        return dataFilePath;
    }

    //获取和ImageView关联的任务
    private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }
}
