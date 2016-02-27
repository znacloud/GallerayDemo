package com.github.znacloud.galleraydemo.presentor;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;


import com.github.znacloud.galleraydemo.model.ImageInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stephan on 2016/1/24.
 */
public class ImageLoader implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String TAG = ImageLoader.class.getSimpleName();
    private final Callback callback;
    private final CursorLoader cursorLoader;
    private final LoaderManager loaderManager;
    private ArrayList<ImageInfo> mImageList;

    public ImageLoader(Activity ctx,Callback callback){
        this.callback = callback;
        cursorLoader = new CursorLoader(ctx, MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.ImageColumns.TITLE,
                             MediaStore.Images.ImageColumns.DATE_ADDED,
                             MediaStore.Images.ImageColumns.SIZE,
                             MediaStore.Images.ImageColumns.DATA},null,null,
                    MediaStore.Images.ImageColumns.DATE_ADDED);
        loaderManager = ctx.getLoaderManager();
//        loaderManager.initLoader(0, null, this);
        mImageList = new ArrayList();
    }

    public void startLoad(){
        loaderManager.initLoader(0,null,this);
        if(callback != null){
            callback.onLoadStart();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data == null){
            mImageList.clear();
            return;
        }
        Log.i(TAG,"cursor size=>"+data.getCount());
        while (data.moveToNext()){
            ImageInfo info = new ImageInfo();
            info.name = data.getString(data.getColumnIndex(MediaStore.Images.ImageColumns.TITLE));
            info.path = data.getString(data.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
            info.time = data.getString(data.getColumnIndex(MediaStore.Images.ImageColumns.DATE_ADDED));
            info.size = data.getLong(data.getColumnIndex(MediaStore.Images.ImageColumns.SIZE));
            Log.i(TAG, "info=>" + info);

            File tmpTest = new File(info.path);
            if(tmpTest.exists() && info.size > 0) {
                mImageList.add(info);
            }else{
                Log.e("TEST",info.path + "not exists!!!");
            }
        }
        if(callback != null){
            callback.onLoadComplete(mImageList);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mImageList.clear();
        if(callback != null){
            callback.onLoadReset(mImageList);
        }
    }


    public interface Callback{
        void onLoadStart();
        void onLoadComplete(List<ImageInfo> data);
        void onLoadReset(List<ImageInfo> data);
    }
}
