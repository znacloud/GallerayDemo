package com.github.znacloud.galleraydemo.presentor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.github.znacloud.galleraydemo.R;
import com.github.znacloud.galleraydemo.model.AsyncDrawable;
import com.github.znacloud.galleraydemo.model.BitmapWorkerTask;
import com.github.znacloud.galleraydemo.model.ImageInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stephan on 2016/1/24.
 */
public class PhotoAdapter extends BaseAdapter{
    private final Context mContext;
    private final Bitmap mPlaceHolderBitmap;
    private final PhotoBitmapCache mMemCache;
    private List<ImageInfo> mData;

    public PhotoAdapter(Context pContext){
        mContext = pContext;
        mPlaceHolderBitmap = BitmapFactory.decodeResource(pContext.getResources(),
                R.drawable.ic_place_holder);
        mMemCache = PhotoBitmapCache.getCache();
    }

    @Override
    public int getCount() {
        if(mData == null)
        return 0;
        return mData.size();
    }

    @Override
    public ImageInfo getItem(int position) {
        if(mData == null) return null;
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(view == null){
            view = View.inflate(mContext, R.layout.photo_item_layout,null);
        }
        ImageView itemIv= (ImageView)view.findViewById(R.id.siv_item);
        loadBitmap(itemIv, getItem(position).path);
        return view;
    }

    public void setData(List<ImageInfo> pData) {
        if(mData == null){
            mData = new ArrayList<>(10);
        }
        mData.clear();
        mData.addAll(pData);
    }

    private void loadBitmap(ImageView pImageView,String filePath){
        if(cancelPotentialTask(pImageView,filePath)){
            Bitmap cachedBitmap = mMemCache.get(filePath);
            if(cachedBitmap != null){
                pImageView.setImageBitmap(cachedBitmap);
                return;
            }

            BitmapWorkerTask task = new BitmapWorkerTask(pImageView);
            //创建占位Drawable
            final AsyncDrawable asyncDrawable =
                    new AsyncDrawable(mPlaceHolderBitmap, task);
            pImageView.setImageDrawable(asyncDrawable);
            task.execute(filePath);

        }
    }

    private boolean cancelPotentialTask(ImageView pImageView, String pFilePath) {
        BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(pImageView);
        if (bitmapWorkerTask != null) {
            final String bitmapData = bitmapWorkerTask.getDataFilePath();
            // 如果还没有设置图片，或者要设置的图片和原来的不一样
            if (bitmapData == null || !bitmapData.contentEquals(pFilePath)) {
                // 取消原先的任务
                bitmapWorkerTask.cancel(true);
            } else {
                //否则表示同样的任务已经在进行
                return false;
            }
        }
        return true;
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
