package com.github.znacloud.galleraydemo.ui;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.ImageView;

import com.github.znacloud.galleraydemo.R;
import com.github.znacloud.galleraydemo.model.ImageInfo;
import com.github.znacloud.galleraydemo.presentor.ImageLoader;
import com.github.znacloud.galleraydemo.presentor.PhotoAdapter;

import java.lang.ref.WeakReference;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ImageLoader.Callback {

    private GridView mPhotoGv;
    private PhotoAdapter mPhotoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initData();
    }

    private void initViews() {
        mPhotoGv = (GridView)findViewById(R.id.gv_photo);
    }

    private void initData(){
        mPhotoAdapter = new PhotoAdapter(this);
        mPhotoGv.setAdapter(mPhotoAdapter);
        ImageLoader imageLoader = new ImageLoader(this,this);
        imageLoader.startLoad();
    }


    @Override
    public void onLoadStart() {

    }

    @Override
    public void onLoadComplete(List<ImageInfo> data) {
        mPhotoAdapter.setData(data);
        mPhotoAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoadReset(List<ImageInfo> data) {

    }
}
